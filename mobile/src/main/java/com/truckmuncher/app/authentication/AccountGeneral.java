package com.truckmuncher.app.authentication;

import android.accounts.Account;
import android.content.Context;

import com.truckmuncher.app.BuildConfig;
import com.truckmuncher.app.R;

public final class AccountGeneral {

    private AccountGeneral() {
        // No instances
    }

    public static Account getAccount(Context context) {
        return new Account(context.getString(R.string.app_name), BuildConfig.APPLICATION_ID);
    }
}
