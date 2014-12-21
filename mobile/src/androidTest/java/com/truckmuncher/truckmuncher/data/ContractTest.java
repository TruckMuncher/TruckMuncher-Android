package com.truckmuncher.truckmuncher.data;

import android.net.Uri;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

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

    public void testSyncDirective() {
        Uri uri = Contract.TruckConstantEntry.CONTENT_URI;
        assertThat(Contract.needsSync(uri)).isFalse();

        uri = Contract.buildNeedsSync(uri);
        assertThat(Contract.needsSync(uri)).isTrue();
    }

    public void testSuppressNotifyDirective() {
        Uri uri = Contract.TruckConstantEntry.CONTENT_URI;
        assertThat(Contract.isSuppressNotify(uri)).isFalse();

        uri = Contract.suppressNotify(uri);
        assertThat(Contract.isSuppressNotify(uri)).isTrue();
    }

    public void testSanitizeRemovesAllDirectives() {
        Uri base = Contract.TruckConstantEntry.CONTENT_URI;

        Uri uri = Contract.suppressNotify(base);
        uri = Contract.buildNeedsSync(uri);

        assertThat(uri).isNotEqualTo(base);
        assertThat(Contract.sanitize(uri)).isEqualTo(base);
    }
}
