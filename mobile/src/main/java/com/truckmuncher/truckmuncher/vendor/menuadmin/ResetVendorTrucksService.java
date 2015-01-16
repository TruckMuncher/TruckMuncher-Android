package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.truckmuncher.truckmuncher.data.Contract;

public class ResetVendorTrucksService extends IntentService {

    private static final String ARG_VALUES = "content_values";

    public ResetVendorTrucksService() {
        super(ResetVendorTrucksService.class.getSimpleName());
    }

    public static Intent newIntent(Context context, ContentValues[] contentValues) {
        Intent intent = new Intent(context, ResetVendorTrucksService.class);
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

        getContentResolver().bulkInsert(Contract.TRUCK_PROPERTIES_URI, contentValues);

    }
}
