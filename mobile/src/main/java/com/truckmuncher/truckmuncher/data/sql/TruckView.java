package com.truckmuncher.truckmuncher.data.sql;

import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

public class TruckView {

    private TruckView() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String VIEW_CREATE = "create view "
                + Contract.TruckEntry.VIEW_NAME
                + " as select "
                + Contract.TruckConstantEntry.TABLE_NAME + "." + Contract.TruckConstantEntry._ID + ", "
                + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + ", "
                + Contract.TruckConstantEntry.COLUMN_NAME + ", "
                + Contract.TruckConstantEntry.COLUMN_IMAGE_URL + ", "
                + Contract.TruckConstantEntry.COLUMN_KEYWORDS + ", "
                + Contract.TruckConstantEntry.COLUMN_OWNED_BY_CURRENT_USER + ", "
                + Contract.TruckConstantEntry.COLUMN_COLOR_PRIMARY + ", "
                + Contract.TruckConstantEntry.COLUMN_COLOR_SECONDARY + ", "

                + Contract.TruckStateEntry.COLUMN_INTERNAL_ID + ", "
                + Contract.TruckStateEntry.COLUMN_IS_SELECTED_TRUCK + ", "
                + Contract.TruckStateEntry.COLUMN_IS_SERVING + ", "
                + Contract.TruckStateEntry.COLUMN_LATITUDE + ", "
                + Contract.TruckStateEntry.COLUMN_LONGITUDE + ", "
                + Contract.TruckStateEntry.COLUMN_IS_DIRTY

                + " from "
                + Contract.TruckConstantEntry.TABLE_NAME + " inner join "
                + Contract.TruckStateEntry.TABLE_NAME + " on "
                + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + "=" + Contract.TruckStateEntry.COLUMN_INTERNAL_ID
                + ";";

        Timber.i("Creating view: %s", VIEW_CREATE);
        db.execSQL(VIEW_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
