package com.truckmuncher.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

import com.truckmuncher.app.dagger.Dagger_NetworkComponent;
import com.truckmuncher.app.dagger.Dagger_UserComponent;
import com.truckmuncher.app.dagger.NetworkComponent;
import com.truckmuncher.app.dagger.NetworkModule;
import com.truckmuncher.app.dagger.UserComponent;
import com.truckmuncher.app.dagger.UserModule;

public class App extends Application {

    private NetworkComponent networkComponent;
    private UserComponent userComponent;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    /**
     * Show the activity over the lockscreen and wake up the device. If you launched the app manually
     * both of these conditions are already true. If you deployed from the IDE, however, this will
     * save you from hundreds of power button presses and pattern swiping per day!
     */
    public static void riseAndShine(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        PowerManager power = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock lock =
                power.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakeup!");
        lock.acquire();
        lock.release();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LoggerStarter.start(this);

        userComponent = Dagger_UserComponent.builder()
                .userModule(new UserModule(this))
                .build();
        networkComponent = Dagger_NetworkComponent.builder()
                .userComponent(userComponent)
                .networkModule(new NetworkModule(this))
                .build();
    }

    public UserComponent userComponent() {
        return userComponent;
    }

    public NetworkComponent networkComponent() {
        return networkComponent;
    }
}
