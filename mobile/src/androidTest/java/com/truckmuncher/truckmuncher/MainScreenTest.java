package com.truckmuncher.truckmuncher;

import android.test.ActivityInstrumentationTestCase2;

import com.truckmuncher.truckmuncher.test.robots.MainScreenRobot;

import static com.truckmuncher.truckmuncher.test.robots.RobotLoader.withRobot;

public class MainScreenTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainScreenTest() {
        super(MainActivity.class);
    }

    public void testLaunchVendorMode() {
        withRobot(MainScreenRobot.class)
                .launchVendorMode(getInstrumentation().getTargetContext());
    }
}
