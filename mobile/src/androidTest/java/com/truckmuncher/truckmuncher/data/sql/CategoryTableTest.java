package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import java.util.UUID;

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
        values.put(Contract.Category.COLUMN_INTERNAL_ID, internalId);
        values.put(Contract.Category.COLUMN_NAME, "Test Category");
        values.put(Contract.Category.COLUMN_NOTES, "This is a test note.");
        values.put(Contract.Category.ORDER_IN_MENU, 0);
        values.put(Contract.Category.TRUCK_ID, truckId);

        assertThat(db.insert(CategoryTable.TABLE_NAME, null, values)).isEqualTo(1);
        return internalId;
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
