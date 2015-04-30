package com.truckmuncher.app.authentication;

import com.facebook.login.LoginManager;
import com.twitter.sdk.android.Twitter;

import javax.inject.Inject;

public class UserAccount {

    private final AuthTokenPreference authTokenPreference;
    private final UserIdPreference userIdPreference;

    @Inject
    public UserAccount(AuthTokenPreference authTokenPreference, UserIdPreference userIdPreference) {
        this.authTokenPreference = authTokenPreference;
        this.userIdPreference = userIdPreference;
    }

    public void login(String authToken) {
        authTokenPreference.set(authToken);
    }

    public String getAuthToken() {
        return authTokenPreference.get();
    }

    public String getUserId() {
        return userIdPreference.get();
    }

    public void setUserId(String id) {
        userIdPreference.set(id);
    }

    public void logout() {
        Twitter.getSessionManager().clearActiveSession();
        LoginManager.getInstance().logOut();
        authTokenPreference.delete();
        userIdPreference.delete();
    }
}
