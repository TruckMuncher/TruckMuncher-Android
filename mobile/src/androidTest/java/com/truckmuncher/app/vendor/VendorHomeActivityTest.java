package com.truckmuncher.app.vendor;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.truckmuncher.app.test.robots.VendorHomeRobot;
import com.truckmuncher.app.test.rules.GraphReplacementRule;
import com.truckmuncher.app.test.rules.InjectMocksRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import dagger.Module;
import dagger.Provides;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class VendorHomeActivityTest {

    @Rule
    public IntentsTestRule<VendorHomeActivity> activityRule = new IntentsTestRule<>(VendorHomeActivity.class);
    @Rule
    public InjectMocksRule mocksRule = new InjectMocksRule(this);
    @Rule
    public GraphReplacementRule graphRule = new GraphReplacementRule(new TestModule());
    @Mock
    VendorHomeController controller;

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void actionInNoTrucksErrorOpensWebIntent() throws Throwable {
        activityRule.getActivity().showNoTrucksError();

        new VendorHomeRobot(activityRule.getActivity())
                .assertNoTrucksErrorIsVisible()
                .clickAddTruck();

        intended(allOf(
                hasData("https://www.truckmuncher.com/#/login"),
                hasAction(Intent.ACTION_VIEW)));
    }

    @Test
    public void editMenuActionNotifiesController() {
        new VendorHomeRobot(activityRule.getActivity())
                .clickEditMenu();
        verify(controller).onEditMenuClicked();
    }

    @Module(library = true, overrides = true)
    class TestModule {
        @Provides
        VendorHomeController provideVendorHomeController() {
            return controller;
        }
    }
}
