package com.truckmuncher.truckmuncher.authentication;

import android.content.Context;

import com.truckmuncher.truckmuncher.BuildConfig;
import com.truckmuncher.truckmuncher.R;

public final class AccountGeneral {

    public static final String ACCOUNT_TYPE = BuildConfig.PACKAGE_NAME;

    private AccountGeneral() {
        // No instances
    }

    public static String getAccountName(Context context) {
        return context.getString(R.string.app_name);
    }

    public static String getAuthTokenType(Context context) {
        return context.getString(R.string.auth_token_type);
    }

    public static String getAuthTokenTypeLabel(Context context) {
        return context.getString(R.string.auth_token_type_label);
    }
}
