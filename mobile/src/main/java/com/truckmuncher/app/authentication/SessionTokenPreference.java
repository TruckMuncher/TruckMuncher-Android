package com.truckmuncher.app.authentication;

import android.content.SharedPreferences;

import com.truckmuncher.app.data.preferences.Preferences;

import javax.inject.Inject;

import info.metadude.android.typedpreferences.StringPreference;

/**
 * Stores the temporary session key provided by the API. This is shorter-lived than the token given
 * by the social network
 */
public class SessionTokenPreference extends StringPreference {

    @Inject
    public SessionTokenPreference(SharedPreferences preferences) {
        super(preferences, Preferences.SESSION_TOKEN);
    }
}
