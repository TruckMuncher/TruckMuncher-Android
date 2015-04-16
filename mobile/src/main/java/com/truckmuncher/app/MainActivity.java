package com.truckmuncher.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.truckmuncher.app.authentication.AuthenticatorActivity;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.common.RateUs;
import com.truckmuncher.app.customer.CustomerMapFragment;
import com.truckmuncher.app.customer.TruckCluster;
import com.truckmuncher.app.customer.TruckDetailsActivity;
import com.truckmuncher.app.customer.TruckHeaderFragment;
import com.truckmuncher.app.customer.TruckHeaderPagerAdapter;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;
import com.truckmuncher.app.vendor.VendorHomeActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        CustomerMapFragment.OnTruckMarkerClickListener, CustomerMapFragment.OnLocationChangeListener, TruckHeaderFragment.OnTruckHeaderClickListener {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_TRUCK_DETAILS = 2;
    private static final int LOADER_TRUCKS = 0;

    private static final int MIN_LOCATION_CHANGE = 500; // meters

    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @Inject
    UserAccount userAccount;

    private SearchView searchView;

    private String lastQuery;
    private TruckHeaderPagerAdapter pagerAdapter;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        App.get(this).inject(this);

        // If we get an authToken the user is signed in and we can go straight to vendor mode
        if (!TextUtils.isEmpty(userAccount.getAuthToken())) {
            launchVendorMode();
            return;
        }

        handleSearchIntent(getIntent());

        getSupportLoaderManager().initLoader(LOADER_TRUCKS, null, MainActivity.this);

        final CustomerMapFragment mapFragment = (CustomerMapFragment) getSupportFragmentManager().findFragmentById(R.id.customer_map_fragment);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Change the focused truck
                mapFragment.moveTo(pagerAdapter.getTruckId(position));
            }
        });

        RateUs.check(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Intent searchIntent = new Intent(Intent.ACTION_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, (String) null);

                handleSearchIntent(searchIntent);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_vendor_mode) {
            startActivityForResult(AuthenticatorActivity.newIntent(this), REQUEST_LOGIN);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearchIntent(intent);
    }

    /**
     * If the provided intent is a search intent, the query operation it contains will be performed.
     * Currently only the {@link Intent#ACTION_SEARCH} and {@link SearchIntents#ACTION_SEARCH}
     * intents are supported to filter food trucks by the given search query.
     * <p/>
     * If the provided intent is not a search intent, no action will be taken.
     *
     * @param intent that was passed to this activity. This does not have to be a search intent.
     */
    private void handleSearchIntent(@NonNull Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case SearchIntents.ACTION_SEARCH:
                Timber.i("Google Now search");
                searchView.setIconified(false);
                searchView.setQuery(query, true);
                break;
            case Intent.ACTION_SEARCH:
                Timber.i("Action Bar search");
                boolean repeatQuery = query == null ? lastQuery == null : query.equals(lastQuery);

                // Don't need to redo the search if it's the same as last time
                if (!repeatQuery) {
                    CustomerMapFragment mapFragment = (CustomerMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.customer_map_fragment);

                    mapFragment.searchTrucks(query);

                    lastQuery = query;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    launchVendorMode();
                }
                break;
            case REQUEST_TRUCK_DETAILS:
                if (resultCode == RESULT_OK) {
                    String lastTruckId = data.getStringExtra(TruckDetailsActivity.ARG_ENDING_TRUCK);
                    viewPager.setCurrentItem(pagerAdapter.getTruckPosition(lastTruckId));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void launchVendorMode() {
        startActivity(VendorHomeActivity.newIntent(this));
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_TRUCKS:
                String orderBy = null;

                if (currentLocation != null) {
                    // We want the trucks to be ordered by their distance from the user's current location.
                    // Sqlite is relatively limited in its computational powers, so we'll use a modified
                    // Pythagorean Theorem equation to calculate the distance.
                    double longitudeAdjustment = Math.pow(Math.cos(Math.toRadians(currentLocation.latitude)), 2);
                    orderBy = String.format("((%f - latitude) * (%f - latitude) + (%f - longitude) * (%f - longitude) * %f) ASC",
                            currentLocation.latitude, currentLocation.latitude, currentLocation.longitude,
                            currentLocation.longitude, longitudeAdjustment);
                }

                WhereClause whereClause = new WhereClause.Builder()
                        .where(PublicContract.Truck.IS_SERVING, EQUALS, true)
                        .where(PublicContract.Truck.MATCHED_SEARCH, EQUALS, true)
                        .build();

                return new CursorLoader(this, PublicContract.TRUCK_URI, TruckHeaderPagerAdapter.Query.PROJECTION, whereClause.selection, whereClause.selectionArgs, orderBy);
            default:
                throw new RuntimeException("Invalid loader id: " + i);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.getCount() == 0) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty).setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            if (currentLocation != null) {
                pagerAdapter = new TruckHeaderPagerAdapter(getSupportFragmentManager(), cursor, currentLocation);
                viewPager.setAdapter(pagerAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        pagerAdapter = null;
    }

    @Override
    public void onTruckMarkerClick(final TruckCluster truckClusterItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String truckId = truckClusterItem.getTruckId();
                viewPager.setCurrentItem(pagerAdapter.getTruckPosition(truckId));
            }
        });
    }

    @Override
    public void onTruckHeaderClick(String currentTruck) {
        ArrayList<String> truckIds = pagerAdapter.getTruckIds();
        startActivityForResult(TruckDetailsActivity.newIntent(this, truckIds, currentTruck), REQUEST_TRUCK_DETAILS);
    }

    @Override
    public void onLocationChange(LatLng location) {
        LatLng lastLocation = currentLocation;
        currentLocation = location;

        if (lastLocation == null ||
                SphericalUtil.computeDistanceBetween(lastLocation, currentLocation) > MIN_LOCATION_CHANGE) {
            getSupportLoaderManager().restartLoader(LOADER_TRUCKS, null, this);
        }
    }
}
