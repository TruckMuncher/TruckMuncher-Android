package com.truckmuncher.app.dagger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.annotation.Nullable;

import dagger.Component;

@Component(modules = UserModule.class)
public interface UserComponent {
    @Nullable
    Account account();

    AccountManager accountManager();
}
