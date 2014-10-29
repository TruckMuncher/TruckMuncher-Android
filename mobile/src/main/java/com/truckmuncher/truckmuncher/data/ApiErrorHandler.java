package com.truckmuncher.truckmuncher.data;

import android.content.Context;

import com.truckmuncher.api.exceptions.Error;
import com.truckmuncher.truckmuncher.R;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

public class ApiErrorHandler implements ErrorHandler {

    private final Context context;

    public ApiErrorHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public ApiException handleError(RetrofitError cause) {
        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            Timber.e(cause, "Experienced a network error: %s", cause.getMessage());
            return new ApiException(context.getString(R.string.error_network), cause);
        } else if (cause.getResponse().getStatus() == 401) {
            // TODO Logout and make the re-authenticate
            return null;
        } else {
            Error apiError = (Error) cause.getBodyAs(Error.class);
            if (apiError != null) {
                Timber.e(cause, "Error during network request. Error code: %s", apiError.internalCode);
                return new ApiException(apiError.userMessage, cause);
            } else {
                Timber.e(cause, "Error during network request. No response given.");
                return new ApiException(null, cause);
            }
        }
    }
}
