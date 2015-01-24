package com.truckmuncher.app.vendor;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.truckmuncher.app.MainActivity;
import com.truckmuncher.app.test.robots.MainScreenRobot;

import static com.truckmuncher.app.test.robots.RobotLoader.withRobot;

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
                .verifyTitle(getActivity())

                        // TODO we need to figure out how to reset the app state, specifically whether or not we're logged in or not, between tests.
                .launchVendorMode(context);
    }
}
