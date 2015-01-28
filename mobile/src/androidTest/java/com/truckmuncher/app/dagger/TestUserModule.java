package com.truckmuncher.app.dagger;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.truckmuncher.app.authentication.AccountGeneral;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, overrides = true)
public class TestUserModule {

    public static final Account ACCOUNT = new Account("TestAccount", AccountGeneral.ACCOUNT_TYPE);

    private final AccountManager accountManager;

    public TestUserModule(AccountManager manager) {
        accountManager = manager;
    }

    @Provides
    public Account provideAccount() {
        return ACCOUNT;
    }

    @Provides
    @Singleton
    public AccountManager provideAccountManager() {
        return accountManager;
    }
}
