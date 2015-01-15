package com.truckmuncher.truckmuncher.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.truckmuncher.truckmuncher.data.PublicContract;

public class AuthenticatorActivity extends ActionBarAccountAuthenticatorActivity
        implements LoginFragment.LoginSuccessCallback {

    public final static String ARG_ACCOUNT_TYPE = "account_type";
    public final static String ARG_AUTH_TYPE = "auth_type";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "is_adding_account";

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, LoginFragment.newInstance())
                .commit();

        accountManager = AccountManager.get(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoginSuccess(String userName, String authToken) {
        Intent intent = new Intent();

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);

        Account account = AccountGeneral.getAccount(userName);

        // Setup the account to be syncable
        ContentResolver.setSyncAutomatically(account, PublicContract.CONTENT_AUTHORITY, true);
        ContentResolver.setIsSyncable(account, PublicContract.CONTENT_AUTHORITY, 1);

        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, AccountGeneral.AUTH_TOKEN_TYPE, authToken);

        intent.putExtras(result);
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
