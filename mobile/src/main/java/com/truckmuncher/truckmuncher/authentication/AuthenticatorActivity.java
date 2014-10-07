package com.truckmuncher.truckmuncher.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

import com.truckmuncher.truckmuncher.R;

public class AuthenticatorActivity extends AccountAuthenticatorActivity
        implements LoginFragment.LoginSuccessCallback {

    public final static String ARG_ACCOUNT_TYPE = "account_type";
    public final static String ARG_AUTH_TYPE = "auth_type";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "is_adding_account";

    private static final int AUTHORIZATION_CODE = 1;

    private AccountManager accountManager;
    private Account[] accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        accountManager = AccountManager.get(this);
    }

    @Override
    public void onLoginSuccess(String userName, String authToken) {
        Intent intent = new Intent();

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);

        Account account = new Account(userName, AccountGeneral.ACCOUNT_TYPE);

        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, AccountGeneral.getAuthTokenType(this), authToken);

        intent.putExtras(result);
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
