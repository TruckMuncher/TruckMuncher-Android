package com.truckmuncher.truckmuncher.data;

import android.net.Uri;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;
import com.truckmuncher.truckmuncher.test.Assertions;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class ContractTest {

    @Test
    public void convertListToStringWorks() {
        // Test none
        assertThat(Contract.convertListToString(Collections.<String>emptyList()))
                .isEmpty();

        // Test single
        assertThat(Contract.convertListToString(Arrays.asList("cat")))
                .isEqualTo("cat");

        // Test multiple
        assertThat(Contract.convertListToString(Arrays.asList("cat", "dog")))
                .isEqualTo("cat,dog");
    }

    @Test
    public void convertStringToListWorks() {
        String input = "cat,dog,horse";
        assertThat(Contract.convertStringToList(input)).containsExactly("cat", "dog", "horse");
    }

    @Test
    public void suppressNotifyDirectiveWorksBothWays() {
        Uri uri = Uri.parse("http://truckmuncher.com");
        assertThat(Contract.isSuppressNotify(uri)).isFalse();

        uri = Contract.suppressNotify(uri);
        assertThat(Contract.isSuppressNotify(uri)).isTrue();
    }

    @Test
    public void syncToNetworkDirectiveWorksBothWays() {
        Uri uri = Uri.parse("http://truckmuncher.com");
        assertThat(Contract.isSyncToNetwork(uri)).isFalse();

        uri = Contract.syncToNetwork(uri);
        assertThat(Contract.isSyncToNetwork(uri)).isTrue();
    }

    @Test
    public void syncFromNetworkDirectiveWorksBothWays() {
        Uri uri = Uri.parse("http://truckmuncher.com");
        assertThat(Contract.isSyncFromNetwork(uri)).isFalse();

        uri = Contract.syncFromNetwork(uri);
        assertThat(Contract.isSyncFromNetwork(uri)).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void suppressNotifyDirectiveCannotBeUsedWithSyncToNetworkDirective() {
        Uri uri = Contract.syncToNetwork(Uri.parse("http://truckmuncher.com"));
        Contract.suppressNotify(uri);
    }

    @Test(expected = IllegalStateException.class)
    public void syncToNetworkDirectiveCannotBeUsedWithSuppressNotifyDirective() {
        Uri uri = Contract.suppressNotify(Uri.parse("http://truckmuncher.com"));
        Contract.syncToNetwork(uri);
    }

    /*
     * TRUCK STATE
     */
    @Test
    public void truckStateUriHasContentScheme() {
        Assertions.assertThat(Contract.TRUCK_STATE_URI).hasContentScheme();
    }

    @Test
    public void truckStateUriHasCorrectPath() {
        Assertions.assertThat(Contract.TRUCK_STATE_URI).hasPath("/truck_state");
    }

    @Test
    public void truckStateUriHasCorrectAuthority() {
        Assertions.assertThat(Contract.TRUCK_STATE_URI).hasAuthority(PublicContract.CONTENT_AUTHORITY);
    }
}
