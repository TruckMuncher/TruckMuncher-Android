package com.truckmuncher.app.data;

import android.util.Base64;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import retrofit.RequestInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ApiRequestInterceptorTest {

    ApiRequestInterceptor interceptor;
    StubFacade facade;

    @Before
    public void setUp() {
        facade = new StubFacade();
    }

    @Test
    public void timeStampHeaderIsAdded() {
        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey(ApiRequestInterceptor.HEADER_TIMESTAMP);
        String value = facade.headers.get(ApiRequestInterceptor.HEADER_TIMESTAMP);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            format.parse(value);
        } catch (ParseException e) {
            Assertions.fail("Unable to parse the header timestamp", e);
        }
    }

    @Test
    public void nonceHeaderIsAdded() {
        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey(ApiRequestInterceptor.HEADER_NONCE);
        String value = facade.headers.get(ApiRequestInterceptor.HEADER_NONCE);
        assertThat(Base64.decode(value, Base64.DEFAULT)).hasSize(32);
    }

    @Test
    public void nonceAreUnique() {
        Set<String> nonce = new HashSet<>(10000);
        for (int i = 0; i < 10000; i++) {
            interceptor.intercept(facade);
            String value = facade.headers.get(ApiRequestInterceptor.HEADER_NONCE);
            assertThat(nonce.add(value)).isTrue();
        }
    }

    public static class StubFacade implements RequestInterceptor.RequestFacade {

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
