package com.truckmuncher.truckmuncher.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public final class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ARG_FORCE_SYNC = "force_sync";
    private final Context context;
    private final ContentResolver contentResolver;
    private final ConnectivityManager connectivityManager;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        contentResolver = context.getContentResolver();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        long startTime = System.currentTimeMillis();
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (extras.getBoolean(ARG_FORCE_SYNC, false) || mWifi.isConnected()) {
            // TODO sync
        }
        long endTime = System.currentTimeMillis();
    }
}
