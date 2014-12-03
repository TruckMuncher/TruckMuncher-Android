package com.truckmuncher.truckmuncher.test.robots;

import android.app.Activity;
import android.content.Context;

import com.truckmuncher.truckmuncher.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.assertj.android.api.Assertions.assertThat;

public class MainScreenRobot extends ScreenRobot {
    public MainScreenRobot launchVendorMode(Context context) {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(R.string.action_vendor_mode)).perform(click());
        return this;
    }

    public MainScreenRobot verifyTitle(Activity activity) {
        assertThat(activity).hasTitle(R.string.app_name);
        return this;
    }
}
