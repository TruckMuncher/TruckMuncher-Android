package com.truckmuncher.app.customer;


import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.common.api.GoogleApiClient;
import com.truckmuncher.app.MainActivity;
import com.truckmuncher.app.R;

import static org.assertj.core.api.Assertions.assertThat;

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

    public void testApiClientConnected() {
        CustomerMapFragment mapFragment = (CustomerMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.customer_map_fragment);

        assertThat(mapFragment.apiClient.isConnected()).isTrue();
    }

    public void testApiClientDisconnected() {
        CustomerMapFragment mapFragment = (CustomerMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.customer_map_fragment);

        GoogleApiClient apiClient = mapFragment.apiClient;

        getInstrumentation().callActivityOnStop(activity);
        getInstrumentation().waitForIdleSync();

        assertThat(apiClient.isConnected()).isFalse();
    }
}
