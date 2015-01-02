package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;

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
        ContentValues[] contentValues = new ContentValues[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            contentValues[i] = (ContentValues) parcelables[i];
        }
        getContentResolver().bulkInsert(Contract.syncToNetwork(PublicContract.MENU_ITEM_URI), contentValues);
    }
}
