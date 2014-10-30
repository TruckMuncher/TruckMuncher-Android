package com.truckmuncher.truckmuncher.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.test.AndroidTestCase;
import android.util.Base64;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

import org.assertj.core.api.Assertions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit.RequestInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiRequestInterceptorTest extends AndroidTestCase {

    ApiRequestInterceptor interceptor;
    Account account;
    AccountManager accountManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        account = AccountGeneral.getAccount("TestAccount");
        accountManager = AccountManager.get(getContext());
        accountManager.addAccountExplicitly(account, null, null);

        interceptor = new ApiRequestInterceptor(getContext(), account);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        accountManager.setUserData(account, AccountGeneral.USER_DATA_SESSION, null);
        accountManager.removeAccount(account, null, null);
    }

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

    public void testAuthorizationHeaderIsAdded() {
        String session = UUID.randomUUID().toString();
        accountManager.setUserData(account, AccountGeneral.USER_DATA_SESSION, session);

        StubFacade facade = new StubFacade();
        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey("Authorization");
        String value = facade.headers.get("Authorization");
        assertThat(value).isEqualTo("session_token=" + session);
    }

    public void testAuthorizationHeaderWorksOnEmptySession() {
        StubFacade facade = new StubFacade();
        interceptor.intercept(facade);
        assertThat(facade.headers).containsKey("Authorization");
        String value = facade.headers.get("Authorization");
        assertThat(value).isEqualTo("session_token=null");
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
