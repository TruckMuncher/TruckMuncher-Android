package com.truckmuncher.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.actions.SearchIntents;
import com.truckmuncher.app.authentication.AuthenticatorActivity;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.common.RateUs;
import com.truckmuncher.app.customer.AllTrucksFragment;
import com.truckmuncher.app.customer.CustomerMapFragment;
import com.truckmuncher.app.customer.Searchable;
import com.truckmuncher.app.vendor.VendorHomeActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private static final int REQUEST_LOGIN = 1;

    private static final int ITEM_MAP = 0;
    private static final int ITEM_ALL_TRUCKS = 1;
    private static final int ITEM_LOGIN = 2;

    private static final String CONTENT_TAG = "drawer_content";
    private static final String ARG_POSITION = "position";

    @Inject
    UserAccount userAccount;

    private SearchView searchView;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> drawerListAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private String lastQuery;
    private int currentPosition;
    private Fragment currentFragment;

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

        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_POSITION)) {
            currentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        setupNavigationDrawer();

        handleSearchIntent(getIntent());

        selectItem(currentPosition);
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
        outState.putInt(ARG_POSITION, currentPosition);
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
                    launchVendorMode();
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if  (position == currentPosition) {
            drawerLayout.closeDrawer(drawerList);
            return;
        }

        selectItem(position);
    }

    private void setupNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.navigation_drawer_items));
        drawerList.setAdapter(drawerListAdapter);
        drawerList.setOnItemClickListener(this);
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
    }

    private void selectItem(int position) {
        Fragment fragment = null;

        switch(position) {
            case ITEM_MAP:
                fragment = new CustomerMapFragment();
                break;
            case ITEM_ALL_TRUCKS:
                fragment = new AllTrucksFragment();
                break;
            case ITEM_LOGIN:
                startActivityForResult(AuthenticatorActivity.newIntent(this), REQUEST_LOGIN);
                break;
            default:
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

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
        currentPosition = position;
    }
}
