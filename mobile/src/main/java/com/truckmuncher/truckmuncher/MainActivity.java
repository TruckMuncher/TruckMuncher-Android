package com.truckmuncher.truckmuncher;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckProfilesRequest;
import com.truckmuncher.api.trucks.TruckProfilesResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.authentication.AuthenticatorActivity;
import com.truckmuncher.truckmuncher.customer.CursorFragmentStatePagerAdapter;
import com.truckmuncher.truckmuncher.customer.CustomerMapFragment;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.sql.SelectionQueryBuilder;
import com.truckmuncher.truckmuncher.vendor.VendorHomeActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_LOGIN = 1;
    private static final int LOADER_TRUCKS = 0;

    @Inject
    TruckService truckService;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    private SearchView searchView;

    private String lastQuery;
    private CursorFragmentStatePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        App.inject(this, this);

        AccountManager accountManager = AccountManager.get(this);

        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTH_TOKEN_TYPE);

            // If we get an authToken the user is signed in and we can go straight to vendor mode
            if (!TextUtils.isEmpty(authToken)) {
                launchVendorMode(accounts[0].name);
            }
        }

        handleIntent(getIntent());

        truckService.getTruckProfiles(new TruckProfilesRequest(null, null), new Callback<TruckProfilesResponse>() {

            @Override
            public void success(TruckProfilesResponse truckProfilesResponse, Response response) {

                List<Truck> trucks = truckProfilesResponse.trucks;
                ContentValues[] contentValues = new ContentValues[trucks.size()];
                for (int i = 0, max = trucks.size(); i < max; i++) {
                    Truck truck = trucks.get(i);
                    ContentValues values = new ContentValues();
                    values.put(Contract.TruckEntry.COLUMN_INTERNAL_ID, truck.id);
                    values.put(Contract.TruckEntry.COLUMN_NAME, truck.name);
                    values.put(Contract.TruckEntry.COLUMN_IMAGE_URL, truck.imageUrl);
                    values.put(Contract.TruckEntry.COLUMN_KEYWORDS, Contract.convertListToString(truck.keywords));
                    values.put(Contract.TruckEntry.COLUMN_COLOR_PRIMARY, truck.primaryColor);
                    values.put(Contract.TruckEntry.COLUMN_COLOR_SECONDARY, truck.secondaryColor);
                    contentValues[i] = values;
                }

                getContentResolver().bulkInsert(Contract.TruckEntry.CONTENT_URI, contentValues);
                getSupportLoaderManager().initLoader(LOADER_TRUCKS, null, MainActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        final CustomerMapFragment mapFragment = (CustomerMapFragment) getSupportFragmentManager().findFragmentById(R.id.customer_map_fragment);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
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

                handleIntent(searchIntent);

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

        handleIntent(intent);
    }

    /**
     * Handles any intents that are passed to this activity by identifying them and taking the
     * appropriate action. Currently only the {@link Intent#ACTION_SEARCH} and
     * {@link SearchIntents#ACTION_SEARCH} intents are supported to filter food trucks by the given
     * search query.
     * @param intent An intent that was passed to this activity that should be handled.
     */
    private void handleIntent(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);

        switch (intent.getAction()) {
            // Google Now search
            case SearchIntents.ACTION_SEARCH:
                searchView.setIconified(false);
                searchView.setQuery(query, true);
                break;
            // Action bar search
            case Intent.ACTION_SEARCH:
                boolean repeatQuery = query == null ? lastQuery == null : query.equals(lastQuery);

                // Don't need to redo the search if it's the same as last time
                if (!repeatQuery) {
                    CustomerMapFragment mapFragment = (CustomerMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.customer_map_fragment);

                    mapFragment.loadActiveTrucks(query);

                    lastQuery = query;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();

                launchVendorMode(extras.getString(AccountManager.KEY_ACCOUNT_NAME));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void launchVendorMode(String userName) {
        Intent intent = new Intent(this, VendorHomeActivity.class);
        intent.putExtra(VendorHomeActivity.USERNAME, userName);
        startActivity(intent);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_TRUCKS:
                SelectionQueryBuilder query = Contract.TruckCombo.buildServingTrucks();
                return new CursorLoader(this, Contract.TruckCombo.CONTENT_URI, CursorFragmentStatePagerAdapter.Query.PROJECTION, query.toString(), query.getArgsArray(), null);
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
}
