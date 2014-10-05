package com.truckmuncher.truckmuncher.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

import com.truckmuncher.truckmuncher.R;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements LoginFragment.LoginSuccessCallback {

    public final static String ARG_ACCOUNT_TYPE = "account_type";
    public final static String ARG_AUTH_TYPE = "auth_type";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "is_adding_account";

    private AccountManager accountManager;

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

        Account account = new Account(AccountGeneral.getAccountName(this), AccountGeneral.ACCOUNT_TYPE);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, null, null);
            accountManager.setAuthToken(account, AccountGeneral.getAuthTokenType(this), authToken);
        }

        intent.putExtras(result);
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
