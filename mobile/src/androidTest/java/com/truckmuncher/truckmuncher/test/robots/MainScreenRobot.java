package com.truckmuncher.truckmuncher.test.robots;

import android.content.Context;

import com.truckmuncher.truckmuncher.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class MainScreenRobot extends ScreenRobot {
    public MainScreenRobot launchVendorMode(Context context) {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(R.id.action_vendor_mode)).perform(click());
        return this;
    }
}
