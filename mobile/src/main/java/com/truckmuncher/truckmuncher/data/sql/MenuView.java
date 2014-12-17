package com.truckmuncher.truckmuncher.data.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.volkhart.androidutil.data.QueryArgs;

import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.CategoryEntry;
import static com.truckmuncher.truckmuncher.data.Contract.MenuEntry;
import static com.truckmuncher.truckmuncher.data.Contract.MenuItemEntry;
import static com.truckmuncher.truckmuncher.data.Contract.TruckCombo;
import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;

public final class MenuView {

    private MenuView() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String VIEW_CREATE = "create view "
                + MenuEntry.VIEW_NAME
                + " as select "
                + MenuItemEntry.TABLE_NAME + "." + MenuItemEntry._ID + ", "
                + MenuItemEntry.COLUMN_INTERNAL_ID + ", "
                + MenuItemEntry.COLUMN_NAME + ", "
                + MenuItemEntry.COLUMN_PRICE + ", "
                + MenuItemEntry.COLUMN_NOTES + ", "
                + MenuItemEntry.COLUMN_ORDER_IN_CATEGORY + ", "
                + MenuItemEntry.COLUMN_IS_AVAILABLE + ", "

                + CategoryEntry.COLUMN_NAME + ", "
                + CategoryEntry.COLUMN_INTERNAL_ID + ", "
                + CategoryEntry.COLUMN_NOTES + ", "
                + CategoryEntry.COLUMN_ORDER_IN_MENU + ", "

                + TruckEntry.COLUMN_INTERNAL_ID

                + " from "
                + MenuItemEntry.TABLE_NAME + " inner join "
                + CategoryEntry.TABLE_NAME + " on "
                + MenuItemEntry.COLUMN_CATEGORY_ID + "=" + CategoryEntry.COLUMN_INTERNAL_ID
                + " inner join "
                + TruckCombo.VIEW_NAME + " on "
                + CategoryEntry.COLUMN_TRUCK_ID + "=" + TruckCombo.COLUMN_INTERNAL_ID

                + " order by " + CategoryEntry.COLUMN_ORDER_IN_MENU + ", "
                + MenuItemEntry.COLUMN_ORDER_IN_CATEGORY + ";";

        Timber.i("Creating view: %s", VIEW_CREATE);
        db.execSQL(VIEW_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Cursor queryMany(SQLiteDatabase db, Uri uri, String[] projection) {
        QueryArgs args = new QueryArgs(uri);
        return db.query(MenuEntry.VIEW_NAME, projection, args.selection, args.selectionArgs, null, null, null);
    }
}
