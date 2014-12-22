package com.truckmuncher.truckmuncher.data.sql;

import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.MenuEntry;

public final class MenuView {

    private MenuView() {
        // No instances
    }

    public static void onCreate(SQLiteDatabase db) {
        String VIEW_CREATE = "CREATE VIEW "
                + MenuEntry.VIEW_NAME
                + " AS SELECT "
                + "menu_item._id, "
                + "menu_item.id, "
                + "menu_item.name, "
                + "menu_item.price, "
                + "menu_item.notes, "
                + "menu_item.order_in_category, "
                + "menu_item.is_available, "

                + "category.name, "
                + "category.id, "
                + "category.notes, "
                + "category.order_in_menu, "

                + Contract.TruckConstantEntry.COLUMN_INTERNAL_ID

                + " FROM menu_item INNER JOIN category ON "
                + "menu_item.category_id = category.id"
                + " INNER JOIN "
                + Contract.TruckEntry.VIEW_NAME + " ON category.truck_id="
                + Contract.TruckEntry.COLUMN_INTERNAL_ID

                + " ORDER BY category.order_in_menu, menu_item.order_in_category;";

        Timber.i("Creating view: %s", VIEW_CREATE);
        db.execSQL(VIEW_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
