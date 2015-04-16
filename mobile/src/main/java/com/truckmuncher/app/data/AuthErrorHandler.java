package com.truckmuncher.app.data;

import android.content.Context;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class AuthErrorHandler extends ApiErrorHandler {

    @Inject
    public AuthErrorHandler(Context context) {
        super(context);
    }

    @Override
    protected ApiException customHandleError(String message, RetrofitError cause) {
        if (cause.getResponse().getStatus() == 401) {

            // Need to log in again
            return new SocialCredentialsException(message, cause);
        } else {
            return new ApiException(message, cause);
        }
    }
}
