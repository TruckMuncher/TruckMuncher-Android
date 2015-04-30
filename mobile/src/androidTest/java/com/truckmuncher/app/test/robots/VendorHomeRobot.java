package com.truckmuncher.app.test.robots;

import android.app.Activity;
import android.content.Context;

import com.truckmuncher.app.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.android.api.Assertions.assertThat;

public class VendorHomeRobot {

    private final Activity activity;

    public VendorHomeRobot(Activity activity) {
        this.activity = activity;
    }

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

    public VendorHomeRobot assertTitle(String truckName) {
        assertThat(activity).hasTitle(truckName);
        return this;
    }

    public VendorHomeRobot assertServingMode(boolean isServing) {
        onView(withId(R.id.serving_mode)).check(matches(isServing ? isChecked() : isNotChecked()));
        return this;
    }
}
