package com.truckmuncher.truckmuncher.data.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.truckmuncher.api.menu.MenuItemAvailability;
import com.truckmuncher.api.menu.ModifyMenuItemAvailabilityRequest;
import com.truckmuncher.api.trucks.ServingModeRequest;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.ApiManager;
import com.truckmuncher.truckmuncher.data.Contract;

import java.util.ArrayList;
import java.util.List;

import static com.truckmuncher.truckmuncher.data.Contract.MenuItemEntry;
import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;

public final class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final Context context;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            syncTruckServingMode(provider);
            syncMenuItemAvailability(provider);
        } catch (RemoteException e) {
            syncResult.databaseError = true;
        }
    }

    private void syncMenuItemAvailability(ContentProviderClient provider) throws RemoteException {
        Cursor cursor = provider.query(MenuItemEntry.buildDirty(), MenuItemAvailabilityQuery.PROJECTION, null, null, null);
        if (!cursor.moveToFirst()) {
            // Cursor is empty. Probably already synced this.
            return;
        }

        // Construct all the diffs for the request
        final List<MenuItemAvailability> diff = new ArrayList<>(cursor.getCount());
        do {
            diff.add(
                    new MenuItemAvailability.Builder()
                            .menuItemId(cursor.getString(MenuItemAvailabilityQuery.ID))
                            .isAvailable(cursor.getInt(MenuItemAvailabilityQuery.IS_AVAILABLE) == 1)
                            .build()
            );
        } while (cursor.moveToNext());
        cursor.close();

        // Setup and run the request synchronously
        ModifyMenuItemAvailabilityRequest request = new ModifyMenuItemAvailabilityRequest(diff);
        try {
            ApiManager.getMenuService(context).modifyMenuItemAvailability(request);

            // On a successful response, clear the dirty state, but only for values we synced. User might have changed others in the mean time.
            ContentValues[] contentValues = new ContentValues[diff.size()];
            for (int i = 0, max = diff.size(); i < max; i++) {
                MenuItemAvailability availability = diff.get(i);
                ContentValues values = new ContentValues();
                values.put(MenuItemEntry.COLUMN_INTERNAL_ID, availability.menuItemId);
                values.put(MenuItemEntry.COLUMN_IS_DIRTY, false);
                contentValues[i] = values;
            }

            // Since we're clearing an internal state, don't notify listeners
            Uri uri = Contract.buildSuppressNotify(MenuItemEntry.CONTENT_URI);
            provider.bulkInsert(uri, contentValues);
        } catch (ApiException e) {
            // Error has already been logged. If it was network, Let the framework handle it.
            // If it was a server error, we either handle it elsewhere or a repeat request won't make a difference.
        }
    }

    private void syncTruckServingMode(ContentProviderClient provider) throws RemoteException {
        Cursor cursor = provider.query(TruckEntry.buildDirty(), TruckServingModeQuery.PROJECTION, null, null, null);
        if (!cursor.moveToFirst()) {
            // Cursor is empty. Probably already synced this.
            return;
        }

        do {
            ServingModeRequest request = new ServingModeRequest.Builder()
                    .isInServingMode(cursor.getInt(TruckServingModeQuery.IS_SERVING) == 1)
                    .truckId(cursor.getString(TruckServingModeQuery.INTERNAL_ID))
                    .truckLatitude(cursor.getDouble(TruckServingModeQuery.LATITUDE))
                    .truckLongitude(cursor.getDouble(TruckServingModeQuery.LONGITUDE))
                    .build();

            try {
                ApiManager.getTruckService(context).modifyServingMode(request);

                // Clear the dirty state
                ContentValues values = new ContentValues();
                values.put(TruckEntry.COLUMN_IS_DIRTY, false);

                // Since we're clearing an internal state, don't notify listeners
                Uri uri = Contract.buildSuppressNotify(TruckEntry.buildSingleTruck(request.truckId));
                provider.update(uri, values, null, null);
            } catch (ApiException e) {
                // Error has already been logged. If it was network, Let the framework handle it.
                // If it was a server error, we either handle it elsewhere or a repeat request won't make a difference.
            }
        } while (cursor.moveToNext());
        cursor.close();
    }

    private interface MenuItemAvailabilityQuery {
        static final String[] PROJECTION = new String[]{
                MenuItemEntry.COLUMN_INTERNAL_ID,
                MenuItemEntry.COLUMN_IS_AVAILABLE
        };
        static final int ID = 0;
        static final int IS_AVAILABLE = 1;
    }

    interface TruckServingModeQuery {
        static final String[] PROJECTION = new String[]{
                TruckEntry.COLUMN_INTERNAL_ID,
                TruckEntry.COLUMN_IS_SERVING,
                TruckEntry.COLUMN_LATITUDE,
                TruckEntry.COLUMN_LONGITUDE
        };
        static final int INTERNAL_ID = 0;
        static final int IS_SERVING = 1;
        static final int LATITUDE = 2;
        static final int LONGITUDE = 3;
    }
}
