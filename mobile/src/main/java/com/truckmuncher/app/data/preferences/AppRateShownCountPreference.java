package com.truckmuncher.app.data.preferences;

import android.content.SharedPreferences;

import info.metadude.android.typedpreferences.IntPreference;

public class AppRateShownCountPreference extends IntPreference {

    public AppRateShownCountPreference(SharedPreferences preferences) {
        super(preferences, Preferences.APP_RATE_SHOWN_COUNT);
    }

    public void increment() {
        set(get() + 1);
    }
}
