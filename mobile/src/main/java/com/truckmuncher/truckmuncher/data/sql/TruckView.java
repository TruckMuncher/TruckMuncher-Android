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
                + Contract.TruckCombo.VIEW_NAME
                + " as select "
                + Contract.TruckEntry.TABLE_NAME + "." + Contract.TruckEntry._ID + ", "
                + Contract.TruckEntry.COLUMN_INTERNAL_ID + ", "
                + Contract.TruckEntry.COLUMN_NAME + ", "
                + Contract.TruckEntry.COLUMN_IMAGE_URL + ", "
                + Contract.TruckEntry.COLUMN_KEYWORDS + ", "
                + Contract.TruckEntry.COLUMN_OWNED_BY_CURRENT_USER + ", "
                + Contract.TruckEntry.COLUMN_COLOR_PRIMARY + ", "
                + Contract.TruckEntry.COLUMN_COLOR_SECONDARY + ", "

                + Contract.TruckStateEntry.COLUMN_INTERNAL_ID + ", "
                + Contract.TruckStateEntry.COLUMN_IS_SELECTED_TRUCK + ", "
                + Contract.TruckStateEntry.COLUMN_IS_SERVING + ", "
                + Contract.TruckStateEntry.COLUMN_LATITUDE + ", "
                + Contract.TruckStateEntry.COLUMN_LONGITUDE + ", "
                + Contract.TruckStateEntry.COLUMN_IS_DIRTY

                + " from "
                + Contract.TruckEntry.TABLE_NAME + " inner join "
                + Contract.TruckStateEntry.TABLE_NAME + " on "
                + Contract.TruckEntry.COLUMN_INTERNAL_ID + "=" + Contract.TruckStateEntry.COLUMN_INTERNAL_ID
                + ";";

        Timber.i("Creating view: %s", VIEW_CREATE);
        db.execSQL(VIEW_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
