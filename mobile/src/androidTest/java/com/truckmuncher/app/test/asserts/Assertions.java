package com.truckmuncher.app.test.asserts;

import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class Assertions {

    public static RecordedRequestAssert assertThat(RecordedRequest actual) {
        return new RecordedRequestAssert(actual);
    }
}
