package com.truckmuncher.truckmuncher.dagger;

import android.accounts.Account;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

import dagger.Module;
import dagger.Provides;

@Module(library = true, overrides = true)
public class TestUserModule {

    @Provides
    public Account provideAccount() {
        return new Account("TestAccount", AccountGeneral.ACCOUNT_TYPE);
    }

}
