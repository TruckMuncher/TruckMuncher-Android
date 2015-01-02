package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.truckmuncher.truckmuncher.data.PublicContract;

import java.util.Map;

public class MenuAdminServiceHelper {

    void persistMenuDiff(Context context, @NonNull Map<String, Boolean> diff) {

        if (diff.isEmpty()) {
            return;
        }

        ContentValues[] contentValues = new ContentValues[diff.size()];
        int i = 0;
        for (Map.Entry<String, Boolean> entry : diff.entrySet()) {
            ContentValues values = new ContentValues(2);
            values.put(PublicContract.MenuItem.ID, entry.getKey());
            values.put(PublicContract.MenuItem.IS_AVAILABLE, entry.getValue());
            contentValues[i] = values;
            i++;
        }

        Intent intent = InsertMenuItemDiffService.newIntent(context, contentValues);
        context.startService(intent);
    }
}
