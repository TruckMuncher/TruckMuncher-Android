package com.truckmuncher.truckmuncher.data.sync;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.truckmuncher.api.menu.MenuItemAvailability;
import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.menu.ModifyMenuItemAvailabilityRequest;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.sql.Query;

import java.util.ArrayList;
import java.util.List;

public final class MenuItemAvailabilitySyncTask extends SyncTask {

    private final ContentProviderClient provider;
    private final MenuService menuService;
    private final ApiExceptionResolver apiExceptionResolver;

    public MenuItemAvailabilitySyncTask(ContentProviderClient provider, MenuService menuService, ApiExceptionResolver apiExceptionResolver) {
        this.provider = provider;
        this.menuService = menuService;
        this.apiExceptionResolver = apiExceptionResolver;
    }

    @Override
    public ApiResult sync(SyncResult syncResult) throws RemoteException {
        Query query = Contract.MenuItemEntry.buildDirty();
        Cursor cursor = provider.query(Contract.MenuItemEntry.CONTENT_URI, MenuItemAvailabilityQuery.PROJECTION, query.selection, query.selectionArgs, null);
        if (!cursor.moveToFirst()) {
            // Cursor is empty. Probably already synced this.
            cursor.close();
            return ApiResult.OK;
        }

        // Construct all the diffs for the request
        final List<MenuItemAvailability> diff = new ArrayList<>(cursor.getCount());
        do {
            diff.add(
                    new MenuItemAvailability.Builder()
                            .menuItemId(cursor.getString(MenuItemAvailabilityQuery.INTERNAL_ID))
                            .isAvailable(cursor.getInt(MenuItemAvailabilityQuery.IS_AVAILABLE) == 1)
                            .build()
            );
        } while (cursor.moveToNext());
        cursor.close();

        // Setup and run the request synchronously
        ModifyMenuItemAvailabilityRequest request = new ModifyMenuItemAvailabilityRequest(diff);
        try {
            menuService.modifyMenuItemAvailability(request);

            // On a successful response, clear the dirty state, but only for values we synced. User might have changed others in the mean time.
            ContentValues[] contentValues = new ContentValues[diff.size()];
            for (int i = 0, max = diff.size(); i < max; i++) {
                MenuItemAvailability availability = diff.get(i);
                ContentValues values = new ContentValues();
                values.put(Contract.MenuItemEntry.COLUMN_INTERNAL_ID, availability.menuItemId);
                values.put(Contract.MenuItemEntry.COLUMN_IS_DIRTY, false);
                contentValues[i] = values;
            }

            // Since we're clearing an internal state, don't notify listeners
            Uri uri = Contract.suppressNotify(Contract.MenuItemEntry.CONTENT_URI);
            provider.bulkInsert(uri, contentValues);
        } catch (ApiException e) {
            return apiExceptionResolver.resolve(e);
        }
        return ApiResult.OK;
    }

    interface MenuItemAvailabilityQuery {
        static final String[] PROJECTION = new String[]{
                Contract.MenuItemEntry.COLUMN_INTERNAL_ID,
                Contract.MenuItemEntry.COLUMN_IS_AVAILABLE
        };
        static final int INTERNAL_ID = 0;
        static final int IS_AVAILABLE = 1;
    }
}
