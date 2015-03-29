package com.truckmuncher.app.gcm;

import android.content.SharedPreferences;

import com.truckmuncher.app.data.Preferences;

import java.util.UUID;

import info.metadude.android.typedpreferences.StringPreference;

public class InstallationIdPreference extends StringPreference {
    public InstallationIdPreference(SharedPreferences preferences) {
        super(preferences, Preferences.INSTALLATION_ID, UUID.randomUUID().toString());
    }
}
