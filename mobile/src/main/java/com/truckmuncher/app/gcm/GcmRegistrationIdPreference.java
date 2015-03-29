package com.truckmuncher.app.gcm;

import android.content.SharedPreferences;

import com.truckmuncher.app.data.Preferences;

import info.metadude.android.typedpreferences.StringPreference;

public class GcmRegistrationIdPreference extends StringPreference {
    public GcmRegistrationIdPreference(SharedPreferences preferences) {
        super(preferences, Preferences.GCM_REGISTRATION_ID);
    }
}
