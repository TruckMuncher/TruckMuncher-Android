package com.truckmuncher.truckmuncher.data;

import android.util.Base64;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.RequestInterceptor;

public abstract class ApiRequestInterceptor implements RequestInterceptor {

    public static final String HEADER_NONCE = "X-Nonce";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    private final DateFormat formatter;
    private final SecureRandom generator;
    private final byte[] bytes = new byte[32];

    public ApiRequestInterceptor() {
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        generator = new SecureRandom();
    }

    @Override
    public void intercept(RequestFacade request) {

        // Timestamp
        request.addHeader(HEADER_TIMESTAMP, formatter.format(new Date()));

        // Nonce
        generator.nextBytes(bytes);
        String nonce = Base64.encodeToString(bytes, Base64.DEFAULT);
        request.addHeader(HEADER_NONCE, nonce);
    }
}
