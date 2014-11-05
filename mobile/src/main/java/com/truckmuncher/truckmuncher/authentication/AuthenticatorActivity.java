package com.truckmuncher.truckmuncher.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import com.truckmuncher.truckmuncher.data.Contract;

public class AuthenticatorActivity extends AccountAuthenticatorActivity
        implements LoginFragment.LoginSuccessCallback {

    public final static String ARG_ACCOUNT_TYPE = "account_type";
    public final static String ARG_AUTH_TYPE = "auth_type";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "is_adding_account";

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(android.R.id.content, new LoginFragment()).commit();

        accountManager = AccountManager.get(this);
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
        ContentResolver.setSyncAutomatically(account, Contract.CONTENT_AUTHORITY, true);
        ContentResolver.setIsSyncable(account, Contract.CONTENT_AUTHORITY, 1);

        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, AccountGeneral.AUTH_TOKEN_TYPE, authToken);

        intent.putExtras(result);
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
