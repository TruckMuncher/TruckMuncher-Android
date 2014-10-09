package com.truckmuncher.truckmuncher.data.sql;

import android.database.sqlite.SQLiteDatabase;

import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;

public final class TruckTable {

    private TruckTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "create table "
                + TruckEntry.TABLE_NAME
                + "("
                + TruckEntry._ID + " integer primary key autoincrement, "
                + TruckEntry.COLUMN_INTERNAL_ID + " text unique, "
                + TruckEntry.COLUMN_NAME + " text, "
                + TruckEntry.COLUMN_IMAGE_URL + " text, "
                + TruckEntry.COLUMN_KEYWORDS + " text"
                + ");";

        String INDEX_CREATE = "create index "
                + "idx_" + TruckEntry.COLUMN_INTERNAL_ID
                + " on " + TruckEntry.TABLE_NAME
                + " (" + TruckEntry.COLUMN_INTERNAL_ID + ");";

        Timber.d("Creating database: %s", DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);

        Timber.d("Creating index: %s", DATABASE_CREATE);
        db.execSQL(INDEX_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
