package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.UUID;

import static com.truckmuncher.truckmuncher.data.Contract.CategoryEntry;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTableTest {

    /**
     * Targets the latest version of the database schema.
     *
     * @return the internal id of the category
     */
    public static String onCreate(SQLiteDatabase db, String truckId) {
        String internalId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_INTERNAL_ID, internalId);
        values.put(CategoryEntry.COLUMN_NAME, "Test Category");
        values.put(CategoryEntry.COLUMN_NOTES, "This is a test note.");
        values.put(CategoryEntry.COLUMN_ORDER_IN_MENU, 0);
        values.put(CategoryEntry.COLUMN_TRUCK_ID, truckId);

        assertThat(db.insert(CategoryEntry.TABLE_NAME, null, values)).isEqualTo(1);
        return internalId;
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
