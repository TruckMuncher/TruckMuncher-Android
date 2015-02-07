package com.truckmuncher.app;

import android.accounts.Account;
import android.accounts.AccountManager;
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

import com.google.android.gms.actions.SearchIntents;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.truckmuncher.app.authentication.AccountGeneral;
import com.truckmuncher.app.authentication.AuthenticatorActivity;
import com.truckmuncher.app.customer.CursorFragmentStatePagerAdapter;
import com.truckmuncher.app.customer.CustomerMapFragment;
import com.truckmuncher.app.customer.CustomerMenuFragment;
import com.truckmuncher.app.customer.TruckCluster;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;
import com.truckmuncher.app.vendor.VendorHomeActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        CustomerMapFragment.OnTruckMarkerClickListener {

    private static final int REQUEST_LOGIN = 1;
    private static final int LOADER_TRUCKS = 0;

    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.sliding_panel)
    SlidingUpPanelLayout slidingPanel;

    private SearchView searchView;

    private String lastQuery;
    private CursorFragmentStatePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if (BuildConfig.DEBUG) {
            App.riseAndShine(this);
        }

        AccountManager accountManager = AccountManager.get(this);

        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTH_TOKEN_TYPE);

            // If we get an authToken the user is signed in and we can go straight to vendor mode
            if (!TextUtils.isEmpty(authToken)) {
                launchVendorMode();
            }
        }

        handleSearchIntent(getIntent());

        getSupportLoaderManager().initLoader(LOADER_TRUCKS, null, MainActivity.this);

        final CustomerMapFragment mapFragment = (CustomerMapFragment) getSupportFragmentManager().findFragmentById(R.id.customer_map_fragment);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Set the new drag view so scrolling works nicely
                CustomerMenuFragment fragment = pagerAdapter.getItem(position);
                slidingPanel.setDragView(fragment.getHeaderView());

                // Change the focused truck
                mapFragment.moveTo(pagerAdapter.getTruckId(position));
            }
        });
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
            // TODO: @marius said this was wrong :(
            Intent intent = new Intent(this, AuthenticatorActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);

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
     * Handles any intents that are passed to this activity by identifying them and taking the
     * appropriate action. Currently only the {@link Intent#ACTION_SEARCH} and
     * {@link SearchIntents#ACTION_SEARCH} intents are supported to filter food trucks by the given
     * search query.
     *
     * @param intent An intent that was passed to this activity that should be handled.
     */
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
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                launchVendorMode();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void launchVendorMode() {
        startActivity(new Intent(this, VendorHomeActivity.class));
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_TRUCKS:
                WhereClause whereClause = new WhereClause.Builder()
                        .where(PublicContract.Truck.IS_SERVING, EQUALS, true)
                        .and()
                        .where(PublicContract.Truck.MATCHED_SEARCH, EQUALS, true)
                        .build();
                return new CursorLoader(this, PublicContract.TRUCK_URI, CursorFragmentStatePagerAdapter.Query.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
            default:
                throw new RuntimeException("Invalid loader id: " + i);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        pagerAdapter = new CursorFragmentStatePagerAdapter(getSupportFragmentManager(), cursor);
        viewPager.setAdapter(pagerAdapter);
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
                String truckId = truckClusterItem.getTruck().id;
                viewPager.setCurrentItem(pagerAdapter.getTruckPosition(truckId));
            }
        });
    }
}
