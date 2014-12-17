package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.TruckStateEntry;

public final class TruckStateTable {

    private TruckStateTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "create table "
                + TruckStateEntry.TABLE_NAME
                + "("
                + TruckStateEntry._ID + " integer primary key autoincrement, "
                + TruckStateEntry.COLUMN_INTERNAL_ID + " text unique, "
                + TruckStateEntry.COLUMN_IS_SELECTED_TRUCK + " integer default 0, "
                + TruckStateEntry.COLUMN_IS_SERVING + " integer default 0, "
                + TruckStateEntry.COLUMN_LATITUDE + " real, "
                + TruckStateEntry.COLUMN_LONGITUDE + " real, "
                + TruckStateEntry.COLUMN_IS_DIRTY + " integer default 0"
                + ");";

        String INDEX_CREATE = "create index "
                + "idx_" + TruckStateEntry.COLUMN_INTERNAL_ID
                + " on " + TruckStateEntry.TABLE_NAME
                + " (" + TruckStateEntry.COLUMN_INTERNAL_ID + ");";

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
                long rowId = db.insert(Contract.TruckStateEntry.TABLE_NAME, null, values);
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
