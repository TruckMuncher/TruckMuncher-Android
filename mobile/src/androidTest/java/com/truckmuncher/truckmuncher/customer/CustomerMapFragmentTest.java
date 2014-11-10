package com.truckmuncher.truckmuncher.customer;


import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.common.api.GoogleApiClient;
import com.truckmuncher.truckmuncher.MainActivity;
import com.truckmuncher.truckmuncher.R;

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
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Instrumentation.ActivityMonitor monitor =
                    new Instrumentation.ActivityMonitor(MainActivity.class.getName(), null, false);
            getInstrumentation().addMonitor(monitor);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getInstrumentation().waitForIdleSync();
        } catch (Exception e) {
            fail("Unexpected exception when rotating screen: " + e.getLocalizedMessage());
        }
    }

    public void testApiClientConnected() {
        CustomerMapFragment mapFragment = (CustomerMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.customer_map_fragment);

        assertTrue("API client didn't connect.", mapFragment.apiClient.isConnected());
    }

    public void testApiClientDisconnected() {
        CustomerMapFragment mapFragment = (CustomerMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.customer_map_fragment);

        GoogleApiClient apiClient = mapFragment.apiClient;

        getInstrumentation().callActivityOnStop(activity);
        getInstrumentation().waitForIdleSync();

        assertTrue("API client Still connected after parent activity finished.", !apiClient.isConnected());
    }
}
