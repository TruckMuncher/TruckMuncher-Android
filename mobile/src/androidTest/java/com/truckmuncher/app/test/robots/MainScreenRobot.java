package com.truckmuncher.app.test.robots;

import android.app.Activity;

import com.truckmuncher.app.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.android.api.Assertions.assertThat;

public class MainScreenRobot {
    private final Activity activity;

    public MainScreenRobot(Activity activity) {
        this.activity = activity;
    }

    public MainScreenRobot launchVendorMode() {
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withText(R.string.action_vendor_mode)).perform(click());
        return this;
    }

    public MainScreenRobot verifyTitle() {
        assertThat(activity).hasTitle(R.string.app_name);
        return this;
    }
}
