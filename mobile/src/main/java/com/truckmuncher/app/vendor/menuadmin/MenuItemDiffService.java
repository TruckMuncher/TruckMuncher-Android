package com.truckmuncher.app.vendor.menuadmin;

import android.app.IntentService;
import android.app.Notification;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.truckmuncher.app.R;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract.MenuItem;
import com.truckmuncher.app.data.sql.WhereClause;

import java.util.ArrayList;

import timber.log.Timber;

import static com.truckmuncher.app.data.PublicContract.CONTENT_AUTHORITY;
import static com.truckmuncher.app.data.PublicContract.MENU_ITEM_URI;
import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class MenuItemDiffService extends IntentService {

    private static final String ARG_VALUES = "content_values";
    private static final int NOTIFICATION_ID = 9876;

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

            // TODO would be good if this had a pending intent that took the user to where they can resolve the issue
            // The pending intent can also save the "transient" state and apply it on load?
            Notification notification = new NotificationCompat.Builder(this)
                    .setCategory(NotificationCompat.CATEGORY_ERROR)
                    .setContentTitle(getString(R.string.title_menu_item_diff_notification))
                    .setContentText(getString(R.string.error_menu_item_diff))
                    .setSmallIcon(R.drawable.ic_notif_tm)
                    .setColor(getResources().getColor(R.color.pink))
                    .build();
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
        }
    }
}
