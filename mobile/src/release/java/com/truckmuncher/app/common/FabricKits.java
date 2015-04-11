package com.truckmuncher.app.common;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Kit;

public class FabricKits {
    private FabricKits() {
        // No instances
    }

    public static Kit[] list(TwitterAuthConfig authConfig) {
        return new Kit[]{new Twitter(authConfig), new Crashlytics()};
    }
}
