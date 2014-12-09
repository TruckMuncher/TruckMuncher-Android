package com.truckmuncher.truckmuncher;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.authentication.AuthenticatorActivity;
import com.truckmuncher.truckmuncher.vendor.VendorHomeActivity;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_LOGIN = 1;

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountManager = AccountManager.get(this);

        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], AccountGeneral.getAuthTokenType(this));

            // If we get an authToken the user is signed in and we can go straight to vendor mode
            if (!TextUtils.isEmpty(authToken)) {
                launchVendorMode(accounts[0].name);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
