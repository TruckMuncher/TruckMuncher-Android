package com.truckmuncher.debug;

import android.app.Activity;
import android.os.PowerManager;

import com.truckmuncher.app.common.SimpleActivityLifecycleCallbacks;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.FULL_WAKE_LOCK;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

/**
 * Show the activity over the lockscreen and wake up the device. If you launched the app manually
 * both of these conditions are already true. If you deployed from the IDE, however, this will
 * save you from hundreds of power button presses and pattern swiping per day!
 */
public class RiseAndShine extends SimpleActivityLifecycleCallbacks {

    @Override
    public void onActivityStarted(Activity activity) {
        activity.getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);

        PowerManager power = (PowerManager) activity.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock lock =
                power.newWakeLock(FULL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE, "wakeup!");
        lock.acquire();
        lock.release();
    }
}
