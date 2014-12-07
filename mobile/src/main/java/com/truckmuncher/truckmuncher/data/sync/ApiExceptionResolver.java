package com.truckmuncher.truckmuncher.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;

import com.truckmuncher.api.auth.AuthRequest;
import com.truckmuncher.api.auth.AuthResponse;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.authentication.Authenticator;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.AuthenticatedRequestInterceptor;
import com.truckmuncher.truckmuncher.data.ExpiredSessionException;
import com.truckmuncher.truckmuncher.data.SocialCredentialsException;

import javax.inject.Inject;

import retrofit.RetrofitError;
import timber.log.Timber;

public final class ApiExceptionResolver {

    private final AuthService authService;
    private final AccountManager accountManager;

    @Inject
    public ApiExceptionResolver(AuthService authService, AccountManager accountManager) {
        this.authService = authService;
        this.accountManager = accountManager;
    }

    public ApiResult resolve(ApiException exception) {
        if (exception instanceof ExpiredSessionException) {
            return handleExpiredSessionException((ExpiredSessionException) exception);
        } else if (exception instanceof SocialCredentialsException) {
            return handleSocialCredentialsException((SocialCredentialsException) exception);
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

    private ApiResult handleExpiredSessionException(ExpiredSessionException exception) {
        try {
            AuthResponse response = authService.getAuth(new AuthRequest());
            Account account = AccountGeneral.getStoredAccount(accountManager);
            accountManager.setUserData(account, AuthenticatedRequestInterceptor.SESSION_TOKEN, response.sessionToken);
            return ApiResult.SHOULD_RETRY;
        } catch (SocialCredentialsException e) {
            return resolve(e);
        }
    }

    private ApiResult handleSocialCredentialsException(SocialCredentialsException exception) {
        Bundle options = new Bundle();
        options.putBoolean(Authenticator.ARG_NEEDS_SYNC, true);
        Account account = AccountGeneral.getStoredAccount(accountManager);
        accountManager.getAuthToken(
                account,
                AccountGeneral.AUTH_TOKEN_TYPE,
                options,
                true,
                null,
                null
        );
        return ApiResult.NEEDS_USER_INPUT;
    }
}
