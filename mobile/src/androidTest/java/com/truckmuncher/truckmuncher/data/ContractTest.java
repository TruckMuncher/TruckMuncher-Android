package com.truckmuncher.truckmuncher.data;

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
}
