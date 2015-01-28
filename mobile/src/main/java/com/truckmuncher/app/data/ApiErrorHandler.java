package com.truckmuncher.app.data;

import android.content.Context;

import com.truckmuncher.api.exceptions.Error;
import com.truckmuncher.app.R;

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
        }
        String message = null;
        Error apiError = (Error) cause.getBodyAs(Error.class);
        if (apiError != null) {
            Timber.e(cause, "Error during network request. Error code: %s", apiError.internalCode);
            message = apiError.userMessage;
        } else {
            Timber.e(cause, "Error during network request. No response given.");
        }

        return customHandleError(message, cause);
    }

    protected ApiException customHandleError(String message, RetrofitError cause) {
        if (cause.getResponse().getStatus() == 401) {
            // Refresh the session
            return new ExpiredSessionException(message, cause);
        } else {
            return new ApiException(message, cause);
        }
    }
}
