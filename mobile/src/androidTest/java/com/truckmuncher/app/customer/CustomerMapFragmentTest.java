package com.truckmuncher.app.customer;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.test.ActivityInstrumentationTestCase2;

import com.truckmuncher.app.MainActivity;

public class CustomerMapFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private ActionBarActivity activity;

    public CustomerMapFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    public void testCanRotate() {
        try {
            Instrumentation.ActivityMonitor monitor =
                    new Instrumentation.ActivityMonitor(MainActivity.class.getName(), null, false);
            getInstrumentation().addMonitor(monitor);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getInstrumentation().waitForIdleSync();
        } catch (Exception e) {
            fail("Unexpected exception when rotating screen: " + e.getLocalizedMessage());
        }
    }
}
