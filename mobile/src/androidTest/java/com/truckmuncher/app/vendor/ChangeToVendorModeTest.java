package com.truckmuncher.app.vendor;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.truckmuncher.app.MainActivity;
import com.truckmuncher.app.test.robots.MainScreenRobot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChangeToVendorModeTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void launchVendorMode() {
        new MainScreenRobot(activityRule.getActivity())
                .verifyTitle()

                        // TODO we need to figure out how to reset the app state, specifically whether or not we're logged in or not, between tests.
                .launchVendorMode();
    }
}
