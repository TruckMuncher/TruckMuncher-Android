package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.truckmuncher.truckmuncher.data.Contract;

public class InsertMenuItemDiffService extends IntentService {

    static final String ARG_VALUES = "content_values";

    public InsertMenuItemDiffService() {
        super(InsertMenuItemDiffService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentValues[] contentValues = (ContentValues[]) intent.getParcelableArrayExtra(ARG_VALUES);
        getContentResolver().bulkInsert(Contract.buildNeedsSync(Contract.MenuItemEntry.CONTENT_URI), contentValues);
    }
}
