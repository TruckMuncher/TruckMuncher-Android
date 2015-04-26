package com.truckmuncher.app.authentication;

import android.content.SharedPreferences;

import com.truckmuncher.app.data.preferences.Preferences;

import javax.inject.Inject;

import info.metadude.android.typedpreferences.StringPreference;

class UserIdPreference extends StringPreference {

    @Inject
    UserIdPreference(SharedPreferences preferences) {
        super(preferences, Preferences.USER_ID);
    }
}
