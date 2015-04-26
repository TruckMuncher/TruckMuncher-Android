package com.truckmuncher.app.data;

import com.truckmuncher.api.auth.AuthRequest;
import com.truckmuncher.api.auth.AuthResponse;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.app.authentication.SessionTokenPreference;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.data.sync.ApiExceptionResolver;
import com.truckmuncher.app.data.sync.ApiResult;
import com.truckmuncher.testlib.ReadableRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.RetrofitError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(ReadableRunner.class)
public class ApiExceptionResolverTest {

    @Mock
    AuthService authService;
    @Mock
    SessionTokenPreference sessionTokenPreference;
    @Mock
    UserAccount userAccount;
    ApiExceptionResolver resolver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        resolver = new ApiExceptionResolver(authService, sessionTokenPreference, userAccount);
    }

    @Test
    public void expiredSessionRefreshesAuthToken() {
        AuthResponse response = new AuthResponse.Builder()
                .sessionToken("SessionToken")
                .userId("UserId")
                .username("Username")
                .build();
        when(authService.getAuth(any(AuthRequest.class))).thenReturn(response);

        assertThat(resolver.resolve(mock(ExpiredSessionException.class))).isEqualTo(ApiResult.SHOULD_RETRY);
        verify(sessionTokenPreference).set("SessionToken");
        verify(userAccount).setUserId("UserId");
    }

    @Test
    public void errorWithSocialCredentialsResultsInNeedingUserInput() {
        assertThat(resolver.resolve(mock(SocialCredentialsException.class))).isEqualTo(ApiResult.NEEDS_USER_INPUT);
    }

    @Test
    public void conversionErrorsArePermanent() {
        RetrofitError cause = mock(RetrofitError.class);
        when(cause.getKind()).thenReturn(RetrofitError.Kind.CONVERSION);
        ApiException exception = new ApiException("message", cause);
        assertThat(resolver.resolve(exception)).isEqualTo(ApiResult.PERMANENT_ERROR);
    }

    @Test
    public void networkErrorsAreTemporary() {
        RetrofitError cause = mock(RetrofitError.class);
        when(cause.getKind()).thenReturn(RetrofitError.Kind.NETWORK);
        ApiException exception = new ApiException("message", cause);
        assertThat(resolver.resolve(exception)).isEqualTo(ApiResult.TEMPORARY_ERROR);
    }

    @Test
    public void unknownRetrofitErrorsArePermanent() {
        RetrofitError cause = mock(RetrofitError.class);
        when(cause.getKind()).thenReturn(RetrofitError.Kind.UNEXPECTED);
        ApiException exception = new ApiException("message", cause);
        assertThat(resolver.resolve(exception)).isEqualTo(ApiResult.PERMANENT_ERROR);
    }

    @Test
    public void unknownErrorsArePermanent() {
        assertThat(resolver.resolve(mock(ApiException.class))).isEqualTo(ApiResult.PERMANENT_ERROR);
    }
}
