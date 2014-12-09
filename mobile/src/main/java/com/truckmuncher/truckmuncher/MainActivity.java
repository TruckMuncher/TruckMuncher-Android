package com.truckmuncher.truckmuncher;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.actions.SearchIntents;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.authentication.AuthenticatorActivity;
import com.truckmuncher.truckmuncher.vendor.VendorHomeActivity;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_LOGIN = 1;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccountManager accountManager = AccountManager.get(this);

        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], AccountGeneral.getAuthTokenType(this));

            // If we get an authToken the user is signed in and we can go straight to vendor mode
            if (!TextUtils.isEmpty(authToken)) {
                launchVendorMode(accounts[0].name);
            }
        }

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
            case SearchIntents.ACTION_SEARCH:
                searchView.setIconified(false);
                searchView.setQuery(query, true);
                break;
            case Intent.ACTION_SEARCH:
                // TODO: Send search request to API.
                Toast.makeText(this, "You searched " + query, Toast.LENGTH_LONG).show();
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
}
