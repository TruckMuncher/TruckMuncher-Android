package com.truckmuncher.app.vendor.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.truckmuncher.app.R;

public class VendorSettingsFragment extends PreferenceFragment {

    public static VendorSettingsFragment newInstance() {
        return new VendorSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.vendor_preferences);
    }
}
