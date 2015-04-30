package com.truckmuncher.app;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.actions.SearchIntents;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.app.authentication.AuthenticatorActivity;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.common.RateUs;
import com.truckmuncher.app.customer.CustomerMapFragment;
import com.truckmuncher.app.customer.GetFavoriteTrucksService;
import com.truckmuncher.app.customer.Searchable;
import com.truckmuncher.app.customer.TruckListFragment;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.vendor.VendorHomeActivity;
import com.truckmuncher.app.vendor.VendorTrucksService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends ActionBarActivity implements ListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_LOGIN = 1;
    private static final int LOADER_MY_TRUCKS = 0;

    private static final String CONTENT_TAG = "drawer_content";
    private static final String ARG_SELECTED_ITEM_ID = "selected_item_id";

    @Inject
    UserAccount userAccount;

    private SearchView searchView;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private NavigationMenuAdapter drawerListAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private String lastQuery;
    private long currentItemId;
    private Fragment currentFragment;
    private List<Truck> myTrucks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        App.get(this).inject(this);

        // If we get an authToken the user is signed in and we can load their info
        if (isLoggedIn()) {
            loadUserSpecificInfo();
        }

        setupNavigationDrawer();

        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_SELECTED_ITEM_ID)) {
            currentItemId = savedInstanceState.getLong(ARG_SELECTED_ITEM_ID);
            selectItem(currentItemId);
        }

        handleSearchIntent(getIntent());

        getLoaderManager().initLoader(LOADER_MY_TRUCKS, null, this);

        RateUs.check(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerList.setSelection(0);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearchIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_SELECTED_ITEM_ID, currentItemId);
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
                    if (currentFragment instanceof Searchable) {
                        ((Searchable) currentFragment).doSearch(query);
                    }

                    lastQuery = query;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    loadUserSpecificInfo();
                    updateDrawerItems();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (id == currentItemId) {
            drawerLayout.closeDrawer(drawerList);
            return;
        }

        selectItem(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch(i) {
            case LOADER_MY_TRUCKS: {
                return new CursorLoader(this, PublicContract.TRUCK_URI, TruckQuery.PROJECTION,
                        PublicContract.Truck.OWNED_BY_CURRENT_USER + " = 1", new String[]{}, null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case LOADER_MY_TRUCKS: {
                myTrucks = new ArrayList<>();
                cursor.moveToPosition(-1);

                while(cursor.moveToNext()) {
                    Truck truck = new Truck.Builder()
                            .id(cursor.getString(TruckQuery.ID))
                            .name(cursor.getString(TruckQuery.NAME))
                            .build();

                    myTrucks.add(truck);
                }

                updateDrawerItems();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setupNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setOnItemClickListener(this);

        updateDrawerItems();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_closed) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }

        selectItem(drawerListAdapter.getItemId(0));
    }

    private void updateDrawerItems() {
        drawerListAdapter = new NavigationMenuAdapter(this, isLoggedIn(), isVendor());
        drawerList.setAdapter(drawerListAdapter);
    }

    private void selectItem(long id) {
        Fragment fragment = null;

        if (id == NavigationMenuAdapter.ITEM_LIVE_MAP) {
            fragment = new CustomerMapFragment();
        } else if (id == NavigationMenuAdapter.ITEM_ALL_TRUCKS) {
            fragment = new TruckListFragment();
        } else if (id == NavigationMenuAdapter.ITEM_LOGIN) {
            startActivityForResult(AuthenticatorActivity.newIntent(this), REQUEST_LOGIN);
        } else if (id == NavigationMenuAdapter.ITEM_MY_TRUCKS) {
            startActivity(VendorHomeActivity.newIntent(this));
        } else if (id == NavigationMenuAdapter.ITEM_LOGOUT) {
            userAccount.logout();
            updateDrawerItems();
        } else {
            throw new UnsupportedOperationException("Invalid menu item selected");
        }

        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, CONTENT_TAG)
                    .commit();

            currentFragment = fragment;
        }

        int position = drawerListAdapter.getItemPosition(id);

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
        currentItemId = id;
    }

    private void loadUserSpecificInfo() {
        // start loading user specific information
        startService(VendorTrucksService.newIntent(this));
        startService(GetFavoriteTrucksService.newIntent(this));
    }

    private boolean isLoggedIn() {
        return !TextUtils.isEmpty(userAccount.getAuthToken());
    }

    private boolean isVendor() {
        return myTrucks != null && !myTrucks.isEmpty();
    }

    public interface TruckQuery {

        String[] PROJECTION = new String[]{
                PublicContract.Truck.ID,
                PublicContract.Truck.NAME
        };
        int ID = 0;
        int NAME = 1;
    }
}
