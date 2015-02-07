package com.truckmuncher.app.vendor.menuadmin;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Parcelable;
import android.os.RemoteException;

import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.sql.WhereClause;

import java.util.ArrayList;

import timber.log.Timber;

import static com.truckmuncher.app.data.PublicContract.CONTENT_AUTHORITY;
import static com.truckmuncher.app.data.PublicContract.MENU_ITEM_URI;
import static com.truckmuncher.app.data.PublicContract.MenuItem;
import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class MenuItemDiffService extends IntentService {

    private static final String ARG_VALUES = "content_values";

    public MenuItemDiffService() {
        super(MenuItemDiffService.class.getName());
    }

    public static Intent newIntent(Context context, ContentValues[] contentValues) {
        Intent intent = new Intent(context, MenuItemDiffService.class);
        intent.putExtra(ARG_VALUES, contentValues);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Parcelable[] parcelables = intent.getParcelableArrayExtra(ARG_VALUES);
        ArrayList<ContentProviderOperation> operations = new ArrayList<>(parcelables.length);
        for (Parcelable parcelable : parcelables) {
            ContentValues values = (ContentValues) parcelable;

            // Be sure to only update a single row per operation
            WhereClause where = new WhereClause.Builder()
                    .where(MenuItem.ID, EQUALS, values.getAsString(MenuItem.ID))
                    .build();

            // Don't update the ID to avoid extra rowid increments
            values.remove(MenuItem.ID);

            // Flag as dirty
            values.put(Contract.MenuItem.IS_DIRTY, true);
            operations.add(
                    ContentProviderOperation.newUpdate(Contract.syncToNetwork(MENU_ITEM_URI))
                            .withSelection(where.selection, where.selectionArgs)
                            .withValues(values)
                            .build()
            );
        }
        try {
            getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Timber.e(e, "Error applying the menu diff to the database");
            // FIXME notification to the user
        }
    }
}
