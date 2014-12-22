package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

public final class MenuItemTable {

    public static final String TABLE_NAME = "menu_item";

    private MenuItemTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "create table "
                + TABLE_NAME
                + "("
                + Contract.MenuItem._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.MenuItem.ID + " TEXT UNIQUE, "
                + Contract.MenuItem.NAME + " TEXT, "
                + Contract.MenuItem.PRICE + " REAL, "
                + Contract.MenuItem.IS_AVAILABLE + " INTEGER, "
                + Contract.MenuItem.NOTES + " TEXT, "
                + Contract.MenuItem.TAGS + " TEXT, "
                + Contract.MenuItem.ORDER_IN_CATEGORY + " INTEGER, "
                + Contract.MenuItem.CATEGORY_ID + " TEXT, "
                + Contract.MenuItem.IS_DIRTY + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + Contract.MenuItem.CATEGORY_ID + ") "
                + "REFERENCES " + CategoryTable.TABLE_NAME + "(" + Contract.Category.ID + ")"
                + ");";

        Timber.i("Creating database: %s", DATABASE_CREATE);
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
