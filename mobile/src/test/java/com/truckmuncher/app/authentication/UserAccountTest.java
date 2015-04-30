package com.truckmuncher.app.authentication;

import com.truckmuncher.testlib.ReadableRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(ReadableRunner.class)
public class UserAccountTest {

    @Mock
    AuthTokenPreference authTokenPreference;
    @Mock
    UserIdPreference userIdPreference;
    UserAccount userAccount;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userAccount = new UserAccount(authTokenPreference, userIdPreference);
    }

    @Test
    public void loginSetsAuthToken() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(authTokenPreference).set(captor.capture());
        userAccount.login("AuthToken");
        when(authTokenPreference.get()).thenReturn(captor.getValue());

        assertThat(userAccount.getAuthToken()).isEqualTo("AuthToken");
    }

    @Test
    public void userIdIsStored() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(userIdPreference).set(captor.capture());
        userAccount.setUserId("UserId");
        when(userIdPreference.get()).thenReturn(captor.getValue());

        assertThat(userAccount.getUserId()).isEqualTo("UserId");
    }
}
