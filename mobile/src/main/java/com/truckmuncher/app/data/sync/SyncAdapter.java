package com.truckmuncher.app.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.app.App;

import javax.inject.Inject;

public final class SyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject
    TruckService truckService;
    @Inject
    MenuService menuService;
    @Inject
    ApiExceptionResolver apiExceptionResolver;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        App.inject(context, this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        new TruckServingModeSyncTask(provider, truckService, apiExceptionResolver).execute(syncResult);
        new MenuItemAvailabilitySyncTask(provider, menuService, apiExceptionResolver).execute(syncResult);
    }
}
