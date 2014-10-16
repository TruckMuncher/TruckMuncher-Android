package com.truckmuncher.truckmuncher.data;

import com.truckmuncher.api.exceptions.Error;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import timber.log.Timber;

public class ApiErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            Timber.e(cause, "Experienced a network error: %s", cause.getMessage());
            return new ApiException(null, cause);
        } else if (cause.getResponse().getStatus() == 401) {
            // TODO Logout and make the re-authenticate
            return null;
        } else {
            Error apiError = (Error) cause.getBodyAs(Error.class.getComponentType());
            Timber.e(cause, "Error during network request. Error code: %d", apiError.internalCode);
            return new ApiException(apiError.userMessage, cause);
        }
    }
}
