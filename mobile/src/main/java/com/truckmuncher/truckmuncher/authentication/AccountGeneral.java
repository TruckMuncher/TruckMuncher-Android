package com.truckmuncher.truckmuncher.authentication;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.truckmuncher.truckmuncher.BuildConfig;
import com.truckmuncher.truckmuncher.R;

import info.metadude.android.typedpreferences.StringPreference;

public final class AccountGeneral {

    public static final String ACCOUNT_TYPE = BuildConfig.APPLICATION_ID;
    public static final String USER_DATA_SESSION = "session_token";
    private static final String PREF_ACCOUNT_NAME = "account_name";

    private AccountGeneral() {
        // No instances
    }

    public static String getAuthTokenType(Context context) {
        return context.getString(R.string.auth_token_type);
    }

    public static String getAuthTokenTypeLabel(Context context) {
        return context.getString(R.string.auth_token_type_label);
    }

    /**
     * Stores the given account name for use with {@link #getStoredAccount(android.content.Context)}
     */
    public static void setAccountName(Context context, String userName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        new StringPreference(prefs, PREF_ACCOUNT_NAME).set(userName);
    }

    private static String getAccountName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return new StringPreference(prefs, PREF_ACCOUNT_NAME).get();
    }

    /**
     * @return An account for a stored username
     * @throws java.lang.IllegalStateException if no account name is stored
     */
    public static Account getStoredAccount(Context context) {
        String accountName = getAccountName(context);
        if (TextUtils.isEmpty(accountName)) {
            throw new IllegalStateException("An account name has not yet been stored");
        }
        return getAccount(accountName);
    }

    /**
     * Creates an account for the given username. Neither the username or the account are stored
     * or cached anywhere.
     */
    public static Account getAccount(String username) {
        return new Account(username, ACCOUNT_TYPE);
    }
}
