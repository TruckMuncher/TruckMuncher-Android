package com.truckmuncher.app.dagger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.Nullable;

import com.truckmuncher.app.authentication.AccountGeneral;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class UserModule {

    private final Context appContext;

    public UserModule(Context context) {
        appContext = context.getApplicationContext();
    }

    @Provides
    @Nullable
    public Account provideAccount(AccountManager accountManager) {
        return AccountGeneral.getStoredAccount(accountManager);
    }

    @Provides
    public AccountManager provideAccountManager() {
        return AccountManager.get(appContext);
    }
}
