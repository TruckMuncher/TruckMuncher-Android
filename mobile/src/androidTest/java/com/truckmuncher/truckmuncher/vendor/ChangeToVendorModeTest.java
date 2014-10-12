package com.truckmuncher.truckmuncher.vendor;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.truckmuncher.truckmuncher.MainActivity;
import com.truckmuncher.truckmuncher.test.robots.MainScreenRobot;

import static com.truckmuncher.truckmuncher.test.robots.RobotLoader.withRobot;

public class ChangeToVendorModeTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Context context;

    public ChangeToVendorModeTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getTargetContext();
        getActivity();
    }

    public void testLaunchVendorMode() {
        withRobot(MainScreenRobot.class)
                .verifyTitle()
                .launchVendorMode(context);
    }
}
