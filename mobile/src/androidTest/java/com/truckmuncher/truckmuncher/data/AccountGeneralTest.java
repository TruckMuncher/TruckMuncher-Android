package com.truckmuncher.truckmuncher.data;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class AccountGeneralTest extends AndroidTestCase {

    Context context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = new IsolatedContext(null, getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> all = prefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            prefs.edit().remove(entry.getKey()).apply();
        }
    }

    public void testGetAccount() {
        Account account = AccountGeneral.getAccount("Testing");
        assertThat(account.name).isEqualTo("Testing");
        assertThat(account.type).isEqualTo(AccountGeneral.ACCOUNT_TYPE);
    }

    public void testGetStoredAccountThrowsWhenNotSet() {
        try {
            AccountGeneral.getStoredAccount(context);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            // No-op
        }
    }

    public void testGetStoredAccountWorks() {
        AccountGeneral.setAccountName(context, "AccountName");
        Account account = AccountGeneral.getStoredAccount(context);
        assertThat(account.name).isEqualTo("AccountName");
        assertThat(account.type).isEqualTo(AccountGeneral.ACCOUNT_TYPE);
    }
}
