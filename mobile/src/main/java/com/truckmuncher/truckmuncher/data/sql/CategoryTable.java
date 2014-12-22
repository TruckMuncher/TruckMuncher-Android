package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

public final class CategoryTable {

    public static final String TABLE_NAME = "category";

    private CategoryTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "CREATE TABLE "
                + TABLE_NAME
                + "("
                + Contract.Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Category.ID + " TEXT UNIQUE, "
                + Contract.Category.NAME + " TEXT, "
                + Contract.Category.NOTES + " TEXT, "
                + Contract.Category.ORDER_IN_MENU + " INTEGER, "
                + Contract.Category.TRUCK_ID + " TEXT, "
                + "FOREIGN KEY(" + Contract.Category.TRUCK_ID + ") "
                + "REFERENCES " + Contract.TruckConstantEntry.TABLE_NAME + "(" + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + ")"
                + ");";

        Timber.d("Creating database: %s", DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static int bulkInsert(SQLiteDatabase db, ContentValues[] contentValues) {
        int returnCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues values : contentValues) {
                long rowId = db.replace(TABLE_NAME, null, values);
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
