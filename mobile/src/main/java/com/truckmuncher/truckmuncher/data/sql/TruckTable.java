package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.volkhart.androidutil.data.QueryArgs;

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
                + TruckEntry.COLUMN_KEYWORDS + " text, "
                + TruckEntry.COLUMN_IS_SELECTED_TRUCK + " integer default 0, "
                + TruckEntry.COLUMN_OWNED_BY_CURRENT_USER + " integer default 0, "
                + TruckEntry.COLUMN_IS_SERVING + " integer default 0, "
                + TruckEntry.COLUMN_LATITUDE + " real, "
                + TruckEntry.COLUMN_LONGITUDE + " real, "
                + TruckEntry.COLUMN_IS_DIRTY + " integer default 0"
                + ");";

        String INDEX_CREATE = "create index "
                + "idx_" + TruckEntry.COLUMN_INTERNAL_ID
                + " on " + TruckEntry.TABLE_NAME
                + " (" + TruckEntry.COLUMN_INTERNAL_ID + ");";

        Timber.d("Creating database: %s", DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);

        Timber.d("Creating index: %s", INDEX_CREATE);
        db.execSQL(INDEX_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static int bulkInsert(SQLiteDatabase db, ContentValues[] contentValues) {
        int returnCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues values : contentValues) {
                long rowId = db.replace(TruckEntry.TABLE_NAME, null, values);
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

    public static int updateMany(SQLiteDatabase db, Uri uri, ContentValues values) {
        QueryArgs args = new QueryArgs(uri);
        return db.update(TruckEntry.TABLE_NAME, values, args.selection, args.selectionArgs);
    }

    public static Cursor queryMany(SQLiteDatabase db, Uri uri, String[] projection) {
        QueryArgs args = new QueryArgs(uri);
        return db.query(TruckEntry.TABLE_NAME, projection, args.selection, args.selectionArgs, null, null, null);
    }

    public static int deleteMany(SQLiteDatabase db, Uri uri) {
        QueryArgs args = new QueryArgs(uri);
        return db.delete(TruckEntry.TABLE_NAME, args.selection, args.selectionArgs);
    }

    public static Cursor querySingle(SQLiteDatabase db, Uri uri, String[] projection) {
        String selection = TruckEntry.COLUMN_INTERNAL_ID + "=?";
        String[] selectionArgs = new String[]{TruckEntry.getInternalIdFromUri(uri)};
        return db.query(TruckEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
    }

    public static int updateSingle(SQLiteDatabase db, Uri uri, ContentValues values) {
        String selection = TruckEntry.COLUMN_INTERNAL_ID + "=?";
        String[] selectionArgs = new String[]{TruckEntry.getInternalIdFromUri(uri)};
        return db.update(TruckEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public static int deleteSingle(SQLiteDatabase db, Uri uri) {
        String selection = TruckEntry.COLUMN_INTERNAL_ID + "=?";
        String[] selectionArgs = new String[]{TruckEntry.getInternalIdFromUri(uri)};
        return db.delete(TruckEntry.TABLE_NAME, selection, selectionArgs);
    }
}
