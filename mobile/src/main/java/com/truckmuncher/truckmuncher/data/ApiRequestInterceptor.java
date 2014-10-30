package com.truckmuncher.truckmuncher.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Base64;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.RequestInterceptor;

public class ApiRequestInterceptor implements RequestInterceptor {

    private final DateFormat formatter;
    private final SecureRandom generator;
    private final byte[] bytes = new byte[32];
    private final AccountManager accountManager;
    private final Account account;

    public ApiRequestInterceptor(Context context, Account account) {
        PRNGFixes.apply();  // Fix SecureRandom so we can generate nonces
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        generator = new SecureRandom();
        accountManager = AccountManager.get(context);
        this.account = account;
    }

    @Override
    public void intercept(RequestFacade request) {

        // Timestamp
        request.addHeader("X-Timestamp", formatter.format(new Date()));

        // Nonce
        generator.nextBytes(bytes);
        String nonce = Base64.encodeToString(bytes, Base64.DEFAULT);
        request.addHeader("X-Nonce", nonce);

        // Authorization
        String sessionToken = accountManager.getUserData(account, AccountGeneral.USER_DATA_SESSION);
        request.addHeader("Authorization", "session_token=" + sessionToken);
    }
}
