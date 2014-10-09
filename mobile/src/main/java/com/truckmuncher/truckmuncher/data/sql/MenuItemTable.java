package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.volkhart.androidutil.data.QueryArgs;

import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.CategoryEntry;
import static com.truckmuncher.truckmuncher.data.Contract.MenuItemEntry;

public final class MenuItemTable {

    private MenuItemTable() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "create table "
                + MenuItemEntry.TABLE_NAME
                + "("
                + MenuItemEntry._ID + " integer primary key autoincrement, "
                + MenuItemEntry.COLUMN_INTERNAL_ID + " text unique, "
                + MenuItemEntry.COLUMN_NAME + " text, "
                + MenuItemEntry.COLUMN_PRICE + " real, "
                + MenuItemEntry.COLUMN_IS_AVAILABLE + " integer, "
                + MenuItemEntry.COLUMN_NOTES + " text, "
                + MenuItemEntry.COLUMN_TAGS + " text, "
                + MenuItemEntry.COLUMN_ORDER_IN_CATEGORY + " integer, "
                + MenuItemEntry.COLUMN_CATEGORY_ID + " text, "
                + "foreign key(" + MenuItemEntry.COLUMN_CATEGORY_ID + ") "
                + "references " + CategoryEntry.TABLE_NAME + "(" + CategoryEntry.COLUMN_INTERNAL_ID + ")"
                + ");";

        Timber.d("Creating database: %s", DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Cursor queryMany(SQLiteDatabase db, Uri uri, String[] projection) {
        QueryArgs args = new QueryArgs(uri);
        return db.query(MenuItemEntry.TABLE_NAME, projection, args.selection, args.selectionArgs, null, null, null);
    }

    public static int bulkInsert(SQLiteDatabase db, ContentValues[] contentValues) {
        int returnCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues values : contentValues) {
                long rowId = db.replace(MenuItemEntry.TABLE_NAME, null, values);
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
