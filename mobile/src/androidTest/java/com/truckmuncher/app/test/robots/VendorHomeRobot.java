package com.truckmuncher.app.test.robots;

import android.content.Context;

import com.truckmuncher.app.R;
import com.truckmuncher.app.test.Ids;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isChecked;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isNotChecked;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class VendorHomeRobot extends ScreenRobot {

    public VendorHomeRobot launchVendorMode(Context context) {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(R.string.action_vendor_mode)).perform(click());
        return this;
    }

    public VendorHomeRobot toggleServingMode() {
        onView(withId(R.id.serving_mode)).perform(click());
        return this;
    }

    public VendorHomeRobot logout(Context context) {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(R.string.action_logout)).perform(click());
        return this;
    }

    public VendorHomeRobot launchVendorMenu() {
        onView(withId(R.id.action_menu)).perform(click());
        return this;
    }

    public VendorHomeRobot verifyTitle(String truckName) {
        onView(withId(Ids.title())).check(matches(withText(truckName)));
        return this;
    }

    public VendorHomeRobot verifyServingMode(boolean isServing) {
        onView(withId(R.id.serving_mode)).check(matches(isServing ? isChecked() : isNotChecked()));
        return this;
    }
}
