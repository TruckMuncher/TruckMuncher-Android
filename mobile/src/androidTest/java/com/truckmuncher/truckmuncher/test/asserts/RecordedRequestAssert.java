package com.truckmuncher.truckmuncher.test.asserts;

import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.truckmuncher.truckmuncher.data.ApiRequestInterceptor;

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordedRequestAssert extends AbstractAssert<RecordedRequestAssert, RecordedRequest> {

    protected RecordedRequestAssert(RecordedRequest actual) {
        super(actual, RecordedRequestAssert.class);
    }

    public RecordedRequestAssert hasNonceHeader() {
        isNotNull();

        assertThat(actual.getHeader(ApiRequestInterceptor.HEADER_NONCE))
                .isNotEmpty();
        return this;
    }

    public RecordedRequestAssert hasTimestampHeader() {
        isNotNull();

        assertThat(actual.getHeader(ApiRequestInterceptor.HEADER_TIMESTAMP))
                .isNotEmpty();
        return this;
    }
}
