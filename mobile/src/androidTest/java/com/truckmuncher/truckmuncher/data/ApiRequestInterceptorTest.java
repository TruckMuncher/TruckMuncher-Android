package com.truckmuncher.truckmuncher.data;

import android.util.Base64;

import junit.framework.TestCase;

import org.assertj.core.api.Assertions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit.RequestInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiRequestInterceptorTest extends TestCase {

    private final ApiRequestInterceptor interceptor = new ApiRequestInterceptor();

    public void testTimeStampHeaderIsAdded() {
        StubFacade facade = new StubFacade();
        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey("X-Timestamp");
        String value = facade.headers.get("X-Timestamp");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            format.parse(value);
        } catch (ParseException e) {
            Assertions.fail("Unable to parse the header timestamp", e);
        }
    }

    public void testNonceHeaderIsAdded() {
        StubFacade facade = new StubFacade();
        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey("X-Nonce");
        String value = facade.headers.get("X-Nonce");
        assertThat(Base64.decode(value, Base64.DEFAULT)).hasSize(32);
    }

    private class StubFacade implements RequestInterceptor.RequestFacade {

        Map<String, String> headers = new HashMap<>();

        @Override
        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public void addPathParam(String name, String value) {
        }

        @Override
        public void addEncodedPathParam(String name, String value) {
        }

        @Override
        public void addQueryParam(String name, String value) {
        }

        @Override
        public void addEncodedQueryParam(String name, String value) {
        }
    }
}
