package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public final class MenuItemTable {

    private MenuItemTable() {
        // No instances
    }

    public static int bulkInsert(SQLiteDatabase db, ContentValues[] contentValues) {
        int returnCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues values : contentValues) {
                long rowId = db.replace(Tables.MENU_ITEM, null, values);
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
