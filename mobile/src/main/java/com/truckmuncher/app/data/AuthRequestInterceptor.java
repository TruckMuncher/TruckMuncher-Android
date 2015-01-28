package com.truckmuncher.app.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.truckmuncher.app.authentication.AccountGeneral;

/**
 * Used for the /auth route of the api
 */
public class AuthRequestInterceptor extends ApiRequestInterceptor {

    private final AccountManager accountManager;
    private final Account account;

    public AuthRequestInterceptor(Context context, Account account) {
        super();
        accountManager = AccountManager.get(context);
        this.account = account;
    }

    @Override
    public void intercept(RequestFacade request) {
        super.intercept(request);

        // Authorization
        String authToken = accountManager.peekAuthToken(account, AccountGeneral.AUTH_TOKEN_TYPE);
        request.addHeader(HEADER_AUTHORIZATION, authToken);
    }
}
