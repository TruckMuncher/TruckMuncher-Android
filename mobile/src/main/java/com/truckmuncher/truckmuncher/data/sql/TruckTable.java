package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

public final class TruckTable {

    private TruckTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "create table "
                + Contract.TruckConstantEntry.TABLE_NAME
                + "("
                + Contract.TruckConstantEntry._ID + " integer primary key autoincrement, "
                + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + " text unique, "
                + Contract.TruckConstantEntry.COLUMN_NAME + " text, "
                + Contract.TruckConstantEntry.COLUMN_IMAGE_URL + " text, "
                + Contract.TruckConstantEntry.COLUMN_KEYWORDS + " text, "
                + Contract.TruckConstantEntry.COLUMN_OWNED_BY_CURRENT_USER + " integer default 0, "
                + Contract.TruckConstantEntry.COLUMN_COLOR_PRIMARY + " text, "
                + Contract.TruckConstantEntry.COLUMN_COLOR_SECONDARY + " text"
                + ");";

        String INDEX_CREATE = "create index "
                + "idx_" + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID
                + " on " + Contract.TruckConstantEntry.TABLE_NAME
                + " (" + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + ");";

        Timber.i("Creating database: %s", DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);

        Timber.i("Creating index: %s", INDEX_CREATE);
        db.execSQL(INDEX_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static int bulkInsert(SQLiteDatabase db, ContentValues[] contentValues) {
        int returnCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues values : contentValues) {
                long rowId = db.replace(Contract.TruckConstantEntry.TABLE_NAME, null, values);
                if (rowId != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return returnCount;
    }
}
