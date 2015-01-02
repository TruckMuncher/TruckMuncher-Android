package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.content.ContentValues;
import android.content.Intent;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;
import com.truckmuncher.truckmuncher.data.PublicContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class MenuAdminServiceHelperTest {

    private MenuAdminServiceHelper helper;

    @Before
    public void setUp() {
        helper = new MenuAdminServiceHelper();
    }

    @Test(expected = NullPointerException.class)
    public void persistMenuDiffRejectsNullDiff() {
        helper.persistMenuDiff(Robolectric.application, null);
    }

    @Test
    public void persistMenuDiffDoesNotLaunchIntentIfDiffIsEmpty() {
        helper.persistMenuDiff(Robolectric.application, Collections.<String, Boolean>emptyMap());
        Intent actual = Robolectric.shadowOf(Robolectric.application).getNextStartedService();
        assertThat(actual).isNull();
    }

    @Test
    public void persistMenuDiffLaunchesIntentWithExpectedValues() {
        Map<String, Boolean> diff = new HashMap<>();
        diff.put("BLT", true);
        diff.put("Turkey", false);

        ContentValues blt = new ContentValues();
        blt.put(PublicContract.MenuItem.ID, "BLT");
        blt.put(PublicContract.MenuItem.IS_AVAILABLE, true);
        ContentValues turkey = new ContentValues();
        turkey.put(PublicContract.MenuItem.ID, "Turkey");
        turkey.put(PublicContract.MenuItem.IS_AVAILABLE, false);
        ContentValues[] valuesList = new ContentValues[]{blt, turkey};

        helper.persistMenuDiff(Robolectric.application, diff);
        Intent actual = Robolectric.shadowOf(Robolectric.application).getNextStartedService();
        assertThat(actual).isEqualTo(MenuItemDiffService.newIntent(Robolectric.application, valuesList));
    }
}
