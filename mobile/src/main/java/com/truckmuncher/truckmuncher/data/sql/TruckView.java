package com.truckmuncher.truckmuncher.data.sql;

import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

public class TruckView {

    private TruckView() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String VIEW_CREATE = "CREATE VIEW "
                + Contract.TruckEntry.VIEW_NAME
                + " AS SELECT "
                + Contract.TruckConstantEntry.TABLE_NAME + "." + Contract.TruckConstantEntry._ID + ", "
                + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + ", "
                + Contract.TruckConstantEntry.COLUMN_NAME + ", "
                + Contract.TruckConstantEntry.COLUMN_IMAGE_URL + ", "
                + Contract.TruckConstantEntry.COLUMN_KEYWORDS + ", "
                + Contract.TruckConstantEntry.COLUMN_OWNED_BY_CURRENT_USER + ", "
                + Contract.TruckConstantEntry.COLUMN_COLOR_PRIMARY + ", "
                + Contract.TruckConstantEntry.COLUMN_COLOR_SECONDARY + ", "

                + "truck_state.id, "
                + "truck_state.is_selected, "
                + "truck_state.is_serving, "
                + "truck_state.latitude, "
                + "truck_state.longitude, "
                + "truck_state.is_dirty"

                + " FROM "
                + Contract.TruckConstantEntry.TABLE_NAME + " INNER JOIN "
                + TruckStateTable.TABLE_NAME + " ON "
                + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID + "=truck_state.id;";

        Timber.i("Creating view: %s", VIEW_CREATE);
        db.execSQL(VIEW_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
