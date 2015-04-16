package com.truckmuncher.app.data;

import com.truckmuncher.app.authentication.UserAccount;

import javax.inject.Inject;

/**
 * Used for the /auth route of the api
 */
public class AuthRequestInterceptor extends ApiRequestInterceptor {

    private final UserAccount userAccount;

    @Inject
    public AuthRequestInterceptor(UserAccount userAccount) {
        super();
        this.userAccount = userAccount;
    }

    @Override
    public void intercept(RequestFacade request) {
        super.intercept(request);

        // Authorization
        request.addHeader(HEADER_AUTHORIZATION, userAccount.getAuthToken());
    }
}
