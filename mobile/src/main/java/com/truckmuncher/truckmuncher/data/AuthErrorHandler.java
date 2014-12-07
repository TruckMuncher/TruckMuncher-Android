package com.truckmuncher.truckmuncher.data;

import android.content.Context;

import retrofit.RetrofitError;

public class AuthErrorHandler extends ApiErrorHandler {

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
