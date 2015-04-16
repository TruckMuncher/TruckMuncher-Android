package com.truckmuncher.app.authentication;

import android.content.SharedPreferences;

import com.truckmuncher.app.data.preferences.Preferences;

import javax.inject.Inject;

import info.metadude.android.typedpreferences.StringPreference;

/**
 * Stores the formatted auth token string that is used by the API
 */
public class AuthTokenPreference extends StringPreference {

    @Inject
    public AuthTokenPreference(SharedPreferences preferences) {
        super(preferences, Preferences.AUTH_TOKEN);
    }
}
