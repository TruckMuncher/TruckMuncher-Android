package com.truckmuncher.truckmuncher.data;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;
import com.truckmuncher.truckmuncher.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.truckmuncher.truckmuncher.test.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class PublicContractTest {

    @Test
    public void authorityIsAsExpected() {
        assertThat(Contract.CONTENT_AUTHORITY).isEqualTo(BuildConfig.APPLICATION_ID + ".provider");
    }

    /*
     * CATEGORY
     */
    @Test
    public void categoryUriHasContentScheme() {
        assertThat(PublicContract.CATEGORY_URI).hasContentScheme();
    }

    @Test
    public void categoryUriHasCorrectPath() {
        assertThat(PublicContract.CATEGORY_URI).hasPath("/category");
    }

    @Test
    public void categoryUriHasCorrectAuthority() {
        assertThat(PublicContract.CATEGORY_URI).hasAuthority(Contract.CONTENT_AUTHORITY);
    }

    @Test
    public void categoryTypeIsCorrect() {
        assertThat(PublicContract.URI_TYPE_CATEGORY).isEqualTo("vnd.android.cursor.dir/vnd.truckmuncher.category");
    }

    /*
     * MENU ITEM
     */
    @Test
    public void menuItemUriHasContentScheme() {
        assertThat(PublicContract.MENU_ITEM_URI).hasContentScheme();
    }

    @Test
    public void menuItemUriHasCorrectPath() {
        assertThat(PublicContract.MENU_ITEM_URI).hasPath("/menu_item");
    }

    @Test
    public void menuItemUriHasCorrectAuthority() {
        assertThat(PublicContract.MENU_ITEM_URI).hasAuthority(Contract.CONTENT_AUTHORITY);
    }

    @Test
    public void menuItemTypeIsCorrect() {
        assertThat(PublicContract.URI_TYPE_MENU_ITEM).isEqualTo("vnd.android.cursor.dir/vnd.truckmuncher.menu_item");
    }

    /*
     * TRUCK
     */
    @Test
    public void truckUriHasContentScheme() {
        assertThat(PublicContract.TRUCK_URI).hasContentScheme();
    }

    @Test
    public void truckUriHasCorrectPath() {
        assertThat(PublicContract.TRUCK_URI).hasPath("/truck");
    }

    @Test
    public void truckUriHasCorrectAuthority() {
        assertThat(PublicContract.TRUCK_URI).hasAuthority(Contract.CONTENT_AUTHORITY);
    }

    @Test
    public void truckTypeIsCorrect() {
        assertThat(PublicContract.URI_TYPE_TRUCK).isEqualTo("vnd.android.cursor.dir/vnd.truckmuncher.truck");
    }
}
