package com.truckmuncher.truckmuncher.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.test.AndroidTestCase;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountGeneralTest extends AndroidTestCase {

    public void testGetAccount() {
        Account account = AccountGeneral.getAccount("Testing");
        assertThat(account.name).isEqualTo("Testing");
        assertThat(account.type).isEqualTo(AccountGeneral.ACCOUNT_TYPE);
    }

    public void testGetStoredAccountIsNullWhenNotSet() throws InterruptedException {
        AccountManager manager = AccountManager.get(getContext());
        Account[] accounts = manager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        // Countdown latch to make sure we block for async execution
        final CountDownLatch latch = new CountDownLatch(accounts.length);
        for (Account account : accounts) {
            manager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> future) {
                    latch.countDown();
                }
            }, null);
        }

        latch.await(10, TimeUnit.SECONDS);
        assertThat(latch.getCount())
                .overridingErrorMessage("%d accounts were not deleted", latch.getCount())
                .isZero();

        assertThat(AccountGeneral.getStoredAccount(manager)).isNull();
    }

    public void testGetStoredAccountReturnsCorrectAccount() {
        Account account = AccountGeneral.getAccount("Testing");
        AccountManager manager = AccountManager.get(getContext());
        manager.addAccountExplicitly(account, null, null);

        assertThat(AccountGeneral.getStoredAccount(manager))
                .isEqualTo(account);
    }
}
