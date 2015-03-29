package com.truckmuncher.app.gcm;

import android.content.SharedPreferences;

import com.truckmuncher.app.data.Preferences;

import info.metadude.android.typedpreferences.IntPreference;

public class AppVersionPreference extends IntPreference {
    public AppVersionPreference(SharedPreferences preferences) {
        super(preferences, Preferences.APP_VERSION, Integer.MIN_VALUE);
    }
}
