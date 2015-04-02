package com.truckmuncher.app.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;

import com.truckmuncher.app.BuildConfig;
import com.truckmuncher.app.R;
import com.truckmuncher.app.data.preferences.AppRateShownCountPreference;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class RateUs {

    private static final int LAUNCHES_UNTIL_PROMPT = 5;
    private static final long MIN_WAIT_BETWEEN_DISPLAYS = 259200000; // 3 days in milliseconds

    public static void check(final Activity activity) {
        AppRate.with(activity)
                .listener(new CountingOnShowListener(activity))
                .retryPolicy(RetryPolicy.EXPONENTIAL)
                .view(R.layout.view_app_rate)
                .text(R.string.app_rate_rate_us)
                .initialLaunchCount(LAUNCHES_UNTIL_PROMPT)
                .minInterval(MIN_WAIT_BETWEEN_DISPLAYS)
                .debug(BuildConfig.DEBUG)
                .checkAndShow();
    }

    static final class CountingOnShowListener implements AppRate.OnShowListener {

        /**
         * How many times AppRate has been shown when the listener is created
         */
        private final AppRateShownCountPreference shownCount;

        CountingOnShowListener(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            shownCount = new AppRateShownCountPreference(prefs);
        }

        @Override
        public void onRateAppShowing(final AppRate appRate, View view) {
            shownCount.increment();
            view.findViewById(R.id.app_rate_never).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View v) {
                    appRate.hide();
                    appRate.neverShowAgain();
                    // TODO Analytics: log the show count
                }
            });
        }

        @Override
        public void onRateAppDismissed() {
            // TODO Analytics: log the show count
        }

        @Override
        public void onRateAppClicked() {
            // TODO Analytics: log the show count
        }
    }
}
