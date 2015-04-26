package com.truckmuncher.app.common;

import com.truckmuncher.app.BuildConfig;

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

    public static synchronized void start() {
        if (!hasStarted) {
            if (BuildConfig.DEBUG) {
                Timber.plant(new Timber.DebugTree());
            } else {
                Timber.plant(new CrashlyticsTree());
            }

            hasStarted = true;
        }
    }

}
