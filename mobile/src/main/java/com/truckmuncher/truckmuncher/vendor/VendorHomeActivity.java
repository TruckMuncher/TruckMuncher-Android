package com.truckmuncher.truckmuncher.vendor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.truckmuncher.truckmuncher.MainActivity;
import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

public class VendorHomeActivity extends Activity {

    public static final String USERNAME = "VendorHomeActivity.username";

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        accountManager = AccountManager.get(this);

        Bundle extras = getIntent().getExtras();

        if (extras.getString(USERNAME) != null) {
            setTitle(extras.getString(USERNAME));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vendor_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            doLogout();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void doLogout() {
        SocialNetworkManager.getInstance(this).logout();

        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0],
                    AccountGeneral.getAuthTokenType(this));

            if (!TextUtils.isEmpty(authToken)) {
                accountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, authToken);
            }
        }

        exitVendorMode();
    }

    private void exitVendorMode() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
