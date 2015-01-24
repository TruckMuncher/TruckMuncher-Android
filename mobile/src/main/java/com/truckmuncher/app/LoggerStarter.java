package com.truckmuncher.app;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.volkhart.androidutil.reporting.CrashlyticsTree;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Use the logger starter to setup logging from anywhere in the system, ensuring that the logging
 * system is only configured once. This is especially useful in instances where a component may be
 * disconnected from the regular app lifecycle, as is the case with ContentProviders.
 */
public final class LoggerStarter {

    private static boolean hasStarted;

    private LoggerStarter() {
        // No instances
    }

    public static synchronized void start(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context may not be null");
        }
        if (!hasStarted) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET);
            Fabric fabric;

            if (BuildConfig.DEBUG) {
                Timber.plant(new Timber.DebugTree());
                fabric = new Fabric.Builder(context)
                        .kits(new Twitter(authConfig))
                        .build();
            } else {
                fabric = new Fabric.Builder(context)
                        .kits(new Twitter(authConfig), new Crashlytics())
                        .build();
                Timber.plant(new CrashlyticsTree());
            }

            Fabric.with(fabric);

            hasStarted = true;
        }
    }

}
