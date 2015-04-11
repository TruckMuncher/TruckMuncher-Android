package com.truckmuncher.app.data;

import com.truckmuncher.app.authentication.SessionTokenPreference;

import javax.inject.Inject;

/**
 * Used once the user has a session token. Basically on any route except /auth.
 */
public class AuthenticatedRequestInterceptor extends ApiRequestInterceptor {

    public static final String SESSION_TOKEN = "session_token";

    private final SessionTokenPreference preference;

    @Inject
    public AuthenticatedRequestInterceptor(SessionTokenPreference preference) {
        super();
        this.preference = preference;
    }

    @Override
    public void intercept(RequestFacade request) {
        super.intercept(request);

        // Authorization
        String token = preference.get();
        if (token != null) {
            request.addHeader(HEADER_AUTHORIZATION, SESSION_TOKEN + "=" + token);
        }
    }
}
