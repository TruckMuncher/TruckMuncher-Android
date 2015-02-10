package com.truckmuncher.app.data;

import com.truckmuncher.testlib.ReadableRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRunner.class)
public class ApiExceptionTest {

    @Test
    public void getMessage() {
        String message = "My Message";
        ApiException e = new ApiException(message, null);
        assertThat(e.getMessage()).isEqualTo(message);
    }
}
