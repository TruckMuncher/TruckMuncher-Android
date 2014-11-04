package com.truckmuncher.truckmuncher.test.asserts;

import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class Assertions {

    public static RecordedRequestAssert assertThat(RecordedRequest actual) {
        return new RecordedRequestAssert(actual);
    }
}
