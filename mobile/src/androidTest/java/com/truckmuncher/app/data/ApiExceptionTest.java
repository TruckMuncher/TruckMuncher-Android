package com.truckmuncher.app.data;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiExceptionTest extends TestCase {

    public void testGetMessage() {
        String message = "My Message";
        ApiException e = new ApiException(message, null);
        assertThat(e.getMessage()).isEqualTo(message);
    }
}
