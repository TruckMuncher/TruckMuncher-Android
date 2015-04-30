package com.truckmuncher.app.customer;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.truckmuncher.app.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CustomerMapFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void canRotate() {
        Instrumentation.ActivityMonitor monitor =
                new Instrumentation.ActivityMonitor(MainActivity.class.getName(), null, false);
        InstrumentationRegistry.getInstrumentation().addMonitor(monitor);
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }
}
