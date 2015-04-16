package com.truckmuncher.app.data;

import com.truckmuncher.app.authentication.SessionTokenPreference;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(ReadableRobolectricTestRunner.class)
public class AuthenticatedRequestInterceptorTest extends ApiRequestInterceptorTest {

    @Mock
    SessionTokenPreference preference;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        interceptor = new AuthenticatedRequestInterceptor(preference);
    }

    @Test
    public void authorizationHeaderIsAdded() {
        String sessionToken = UUID.randomUUID().toString();
        when(preference.get()).thenReturn(sessionToken);

        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey(ApiRequestInterceptor.HEADER_AUTHORIZATION);
        String value = facade.headers.get(ApiRequestInterceptor.HEADER_AUTHORIZATION);
        assertThat(value).isEqualTo(AuthenticatedRequestInterceptor.SESSION_TOKEN + "=" + sessionToken);
    }

    @Test
    public void authorizationHeaderWorksOnEmptySession() {
        when(preference.get()).thenReturn("");

        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey(ApiRequestInterceptor.HEADER_AUTHORIZATION);
        String value = facade.headers.get(ApiRequestInterceptor.HEADER_AUTHORIZATION);
        assertThat(value).isEqualTo(AuthenticatedRequestInterceptor.SESSION_TOKEN + "=");
    }
}
