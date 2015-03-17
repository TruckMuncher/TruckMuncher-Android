package com.truckmuncher.app.vendor.menuadmin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.truckmuncher.app.R;
import com.truckmuncher.app.data.PublicContract;

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

        Intent intent = MenuItemDiffService.newIntent(context, contentValues);
        context.startService(intent);

        Toast.makeText(context, R.string.saving_menu_availability, Toast.LENGTH_SHORT).show();
    }
}
