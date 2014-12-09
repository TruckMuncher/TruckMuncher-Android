package com.truckmuncher.truckmuncher.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.Nullable;

import com.truckmuncher.truckmuncher.BuildConfig;
import com.truckmuncher.truckmuncher.R;

public final class AccountGeneral {

    public static final String ACCOUNT_TYPE = BuildConfig.APPLICATION_ID;
    public static final String USER_DATA_SESSION = "session_token";
    public static final String AUTH_TOKEN_TYPE = "TruckMuncher";    // Only have 1 security level

    private AccountGeneral() {
        // No instances
    }

    public static String getAuthTokenTypeLabel(Context context) {
        return context.getString(R.string.auth_token_type_label);
    }

    /**
     * @return An account for a stored username. Null if none has been stored
     */
    @Nullable
    public static Account getStoredAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accounts[0];
        } else {
            return null;
        }
    }

    /**
     * Creates an account for the given username. Neither the username or the account are stored
     * or cached anywhere.
     */
    public static Account getAccount(String username) {
        return new Account(username, ACCOUNT_TYPE);
    }
}
