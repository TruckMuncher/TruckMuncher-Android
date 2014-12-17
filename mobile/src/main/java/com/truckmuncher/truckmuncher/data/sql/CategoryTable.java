package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.CategoryEntry;

public final class CategoryTable {

    private CategoryTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "create table "
                + CategoryEntry.TABLE_NAME
                + "("
                + CategoryEntry._ID + " integer primary key autoincrement, "
                + CategoryEntry.COLUMN_INTERNAL_ID + " text unique, "
                + CategoryEntry.COLUMN_NAME + " text, "
                + CategoryEntry.COLUMN_NOTES + " text, "
                + CategoryEntry.COLUMN_ORDER_IN_MENU + " integer, "
                + CategoryEntry.COLUMN_TRUCK_ID + " text, "
                + "foreign key(" + CategoryEntry.COLUMN_TRUCK_ID + ") "
                + "references " + Contract.TruckConstantEntry.TABLE_NAME + "(" + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + ")"
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
                long rowId = db.replace(CategoryEntry.TABLE_NAME, null, values);
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
