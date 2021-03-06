package com.truckmuncher.app.data;

import android.net.Uri;

import com.truckmuncher.app.test.Assertions;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class ContractTest {

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

    /*
     * TRUCK PROPERTIES
     */
    @Test
    public void truckPropertiesUriHasContentScheme() {
        Assertions.assertThat(Contract.TRUCK_PROPERTIES_URI).hasContentScheme();
    }

    @Test
    public void truckPropertiesUriHasCorrectPath() {
        Assertions.assertThat(Contract.TRUCK_PROPERTIES_URI).hasPath("/truck_properties");
    }

    @Test
    public void truckPropertiesUriHasCorrectAuthority() {
        Assertions.assertThat(Contract.TRUCK_PROPERTIES_URI).hasAuthority(PublicContract.CONTENT_AUTHORITY);
    }
}
