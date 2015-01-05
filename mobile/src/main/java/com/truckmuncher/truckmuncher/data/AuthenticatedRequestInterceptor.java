package com.truckmuncher.truckmuncher.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

/**
 * Used once the user has a session token. Basically on any route except /auth.
 */
public class AuthenticatedRequestInterceptor extends ApiRequestInterceptor {

    public static final String SESSION_TOKEN = "session_token";

    private final AccountManager accountManager;

    public AuthenticatedRequestInterceptor(Context context) {
        super();
        accountManager = AccountManager.get(context);
    }

    @Override
    public void intercept(RequestFacade request) {
        super.intercept(request);

        // Authorization
        Account account = AccountGeneral.getStoredAccount(accountManager);
        if (account != null) {
            String sessionToken = accountManager.getUserData(account, AccountGeneral.USER_DATA_SESSION);
            request.addHeader(HEADER_AUTHORIZATION, SESSION_TOKEN + "=" + sessionToken);
        }
    }
}
