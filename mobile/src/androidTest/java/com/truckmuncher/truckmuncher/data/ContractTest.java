package com.truckmuncher.truckmuncher.data;

import android.net.Uri;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class ContractTest extends TestCase {

    public void testConvertListToString() {

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

    public void testConvertStringToList() {
        String input = "cat,dog,horse";
        assertThat(Contract.convertStringToList(input)).containsExactly("cat", "dog", "horse");
    }

    public void testSuppressNotifyDirective() {
        Uri uri = Contract.TruckConstantEntry.CONTENT_URI;
        assertThat(Contract.isSuppressNotify(uri)).isFalse();

        uri = Contract.suppressNotify(uri);
        assertThat(Contract.isSuppressNotify(uri)).isTrue();
    }

    public void testSyncToNetworkDirective() {
        Uri uri = Contract.TruckConstantEntry.CONTENT_URI;
        assertThat(Contract.isSyncToNetwork(uri)).isFalse();

        uri = Contract.syncToNetwork(uri);
        assertThat(Contract.isSyncToNetwork(uri)).isTrue();
    }

    public void testSyncFromNetworkDirective() {
        Uri uri = Contract.TruckConstantEntry.CONTENT_URI;
        assertThat(Contract.isSyncFromNetwork(uri)).isFalse();

        uri = Contract.syncFromNetwork(uri);
        assertThat(Contract.isSyncFromNetwork(uri)).isTrue();
    }

    public void testSuppressNotifyCanNotBeUsedWithSyncToNetwork() {
        Uri uri = Contract.syncToNetwork(Contract.TruckConstantEntry.CONTENT_URI);
        try {
            Contract.suppressNotify(uri);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException ignored) {
        }
    }

    public void testSyncToNetworkCanNotBeUsedWithSuppressNotify() {
        Uri uri = Contract.suppressNotify(Contract.TruckConstantEntry.CONTENT_URI);
        try {
            Contract.syncToNetwork(uri);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException ignored) {
        }
    }
}
