package com.truckmuncher.truckmuncher.dagger;

import android.accounts.Account;
import android.content.Context;

import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class UserModule {

    private final Context appContext;

    public UserModule(Context context) {
        appContext = context.getApplicationContext();
    }

    @Provides
    public Account provideAccount() {
        return AccountGeneral.getStoredAccount(appContext);
    }
}
