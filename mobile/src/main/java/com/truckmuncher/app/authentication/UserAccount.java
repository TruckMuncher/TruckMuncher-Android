package com.truckmuncher.app.authentication;

import com.facebook.login.LoginManager;
import com.twitter.sdk.android.Twitter;

import javax.inject.Inject;

public class UserAccount {

    private final AuthTokenPreference preference;

    @Inject
    public UserAccount(AuthTokenPreference preference) {
        this.preference = preference;
    }

    public void login(String authToken) {
        preference.set(authToken);
    }

    public String getAuthToken() {
        return preference.get();
    }

    public void logout() {
        Twitter.getSessionManager().clearActiveSession();
        LoginManager.getInstance().logOut();
        preference.delete();
    }
}
