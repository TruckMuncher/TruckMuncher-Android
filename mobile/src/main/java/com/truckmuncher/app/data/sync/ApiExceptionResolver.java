package com.truckmuncher.app.data.sync;

import com.truckmuncher.api.auth.AuthRequest;
import com.truckmuncher.api.auth.AuthResponse;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.app.authentication.SessionTokenPreference;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.data.ApiException;
import com.truckmuncher.app.data.ExpiredSessionException;
import com.truckmuncher.app.data.SocialCredentialsException;

import javax.inject.Inject;

import retrofit.RetrofitError;
import timber.log.Timber;

public class ApiExceptionResolver {

    private final AuthService authService;
    private final SessionTokenPreference sessionTokenPreference;
    private final UserAccount userAccount;

    @Inject
    public ApiExceptionResolver(AuthService authService, SessionTokenPreference sessionTokenPreference, UserAccount userAccount) {
        this.authService = authService;
        this.sessionTokenPreference = sessionTokenPreference;
        this.userAccount = userAccount;
    }

    public ApiResult resolve(ApiException exception) {
        if (exception instanceof ExpiredSessionException) {
            return handleExpiredSessionException();
        } else if (exception instanceof SocialCredentialsException) {
            return ApiResult.NEEDS_USER_INPUT;
        } else {
            Throwable t = exception.getCause();
            if (t instanceof RetrofitError) {
                RetrofitError error = (RetrofitError) t;
                switch (error.getKind()) {
                    case NETWORK:
                        return ApiResult.TEMPORARY_ERROR;
                    case CONVERSION:
                        return ApiResult.PERMANENT_ERROR;
                    default:
                        // If it was a server error, we either handle it elsewhere or a repeat request won't make a difference.
                        Timber.e(exception, "Got an unknown %s", RetrofitError.class.getSimpleName());
                        return ApiResult.PERMANENT_ERROR;
                }
            }
            Timber.e(exception, "Got an unknown exception");
            return ApiResult.PERMANENT_ERROR;
        }
    }

    private ApiResult handleExpiredSessionException() {
        try {
            AuthResponse response = authService.getAuth(new AuthRequest());
            sessionTokenPreference.set(response.sessionToken);
            userAccount.setUserId(response.userId);
            return ApiResult.SHOULD_RETRY;
        } catch (SocialCredentialsException e) {
            return resolve(e);
        }
    }
}
