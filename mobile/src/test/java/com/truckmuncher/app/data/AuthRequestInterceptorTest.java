package com.truckmuncher.app.data;

import com.truckmuncher.app.authentication.UserAccount;
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
public class AuthRequestInterceptorTest extends ApiRequestInterceptorTest {

    @Mock
    UserAccount userAccount;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        interceptor = new AuthRequestInterceptor(userAccount);
    }

    @Test
    public void authorizationHeaderIsAdded() {
        String authToken = UUID.randomUUID().toString();
        when(userAccount.getAuthToken()).thenReturn(authToken);

        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey(ApiRequestInterceptor.HEADER_AUTHORIZATION);
        String value = facade.headers.get(ApiRequestInterceptor.HEADER_AUTHORIZATION);
        assertThat(value).isEqualTo(authToken);
    }
}
