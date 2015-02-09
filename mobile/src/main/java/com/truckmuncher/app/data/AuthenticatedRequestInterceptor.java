package com.truckmuncher.app.data;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.truckmuncher.app.authentication.AccountGeneral;

/**
 * Used once the user has a session token. Basically on any route except /auth.
 */
public class AuthenticatedRequestInterceptor extends ApiRequestInterceptor {

    public static final String SESSION_TOKEN = "session_token";

    private final AccountManager accountManager;

    public AuthenticatedRequestInterceptor(AccountManager accountManager) {
        super();
        this.accountManager = accountManager;
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
