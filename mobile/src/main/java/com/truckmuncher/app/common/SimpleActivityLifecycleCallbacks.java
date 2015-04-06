package com.truckmuncher.app.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Serves to reduce noise found in real implementations of {@link android.app.Application.ActivityLifecycleCallbacks}.
 * <p/>
 * Extend this and only override what you care about
 */
public class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
