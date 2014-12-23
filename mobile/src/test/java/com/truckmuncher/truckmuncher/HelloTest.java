package com.truckmuncher.truckmuncher;

import com.truckmuncher.truckmuncher.test.ReadableRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRunner.class)
public class HelloTest {

    @Test
    public void helloWorld() {
        assertThat("Hello, World!").isEqualTo("Hello, World!");
    }
}
