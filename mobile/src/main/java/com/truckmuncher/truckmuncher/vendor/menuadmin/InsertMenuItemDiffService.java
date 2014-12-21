package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Parcelable;

import com.truckmuncher.truckmuncher.data.Contract;

public class InsertMenuItemDiffService extends IntentService {

    static final String ARG_VALUES = "content_values";

    public InsertMenuItemDiffService() {
        super(InsertMenuItemDiffService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Parcelable[] parcelables = intent.getParcelableArrayExtra(ARG_VALUES);
        ContentValues[] contentValues = new ContentValues[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            contentValues[i] = (ContentValues) parcelables[i];
        }
        getContentResolver().bulkInsert(Contract.syncToNetwork(Contract.MenuItemEntry.CONTENT_URI), contentValues);
    }
}
