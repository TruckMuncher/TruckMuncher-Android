package com.truckmuncher.truckmuncher.data;

import android.content.Context;

import com.truckmuncher.api.exceptions.Error;
import com.truckmuncher.truckmuncher.R;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

public class ApiErrorHandler implements ErrorHandler {

    private static final String AUTH_PATH = "/com.truckmuncher.api.auth.AuthService/getAuth";

    private final Context context;

    public ApiErrorHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public ApiException handleError(RetrofitError cause) {
        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            Timber.e(cause, "Experienced a network error: %s", cause.getMessage());
            return new ApiException(context.getString(R.string.error_network), cause);
        }
        String message = null;
        Error apiError = (Error) cause.getBodyAs(Error.class);
        if (apiError != null) {
            Timber.e(cause, "Error during network request. Error code: %s", apiError.internalCode);
            message = apiError.userMessage;
        } else {
            Timber.e(cause, "Error during network request. No response given.");
        }

        if (cause.getResponse().getStatus() == 401) {
            if (cause.getUrl().endsWith(AUTH_PATH)) {

                // Need to log in again
                return new SocialCredentialsException(message, cause);
            } else {

                // Refresh the session
                return new ExpiredSessionException(message, cause);
            }
        } else {
            return new ApiException(message, cause);
        }
    }
}
