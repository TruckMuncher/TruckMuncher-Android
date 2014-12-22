package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

public final class TruckStateTable {

    public static final String TABLE_NAME = "truck_state";

    private TruckStateTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "CREATE TABLE "
                + TABLE_NAME
                + "("
                + Contract.TruckState._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.TruckState.ID + " TEXT UNIQUE, "
                + Contract.TruckState.IS_SELECTED_TRUCK + " INTEGER DEFAULT 0, "
                + Contract.TruckState.IS_SERVING + " INTEGER DEFAULT 0, "
                + Contract.TruckState.LATITUDE + " REAL, "
                + Contract.TruckState.LONGITUDE + " REAL, "
                + Contract.TruckState.IS_DIRTY + " INTEGER DEFAULT 0"
                + ");";

        String INDEX_CREATE = "CREATE INDEX "
                + "idx_" + TABLE_NAME + "_" + Contract.TruckState.ID
                + " ON " + TABLE_NAME
                + " (" + Contract.TruckState.ID + ");";

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
                long rowId = db.insert(TABLE_NAME, null, values);
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
