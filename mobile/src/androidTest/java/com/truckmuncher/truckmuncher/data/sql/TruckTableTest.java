package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.truckmuncher.data.Contract;

import org.assertj.core.api.Assertions;

import java.util.UUID;

public class TruckTableTest {

    /**
     * Targets the latest version of the database schema.
     *
     * @return the internal id of the truck
     */
    public static String onCreate(SQLiteDatabase db) {
        String internalId = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(Contract.TruckConstantEntry.COLUMN_INTERNAL_ID, internalId);
        values.put(Contract.TruckConstantEntry.COLUMN_NAME, "TestTruck1");
        values.put(Contract.TruckConstantEntry.COLUMN_IMAGE_URL, "http://api.truckmuncher.com/images/test");
        values.put(Contract.TruckConstantEntry.COLUMN_KEYWORDS, "vegan, hot dog, lunch");
        values.put(Contract.TruckConstantEntry.COLUMN_IS_SELECTED_TRUCK, true);
        values.put(Contract.TruckConstantEntry.COLUMN_OWNED_BY_CURRENT_USER, true);
        values.put(Contract.TruckConstantEntry.COLUMN_IS_SERVING, false);
        values.put(Contract.TruckConstantEntry.COLUMN_LATITUDE, 88.43);
        values.put(Contract.TruckConstantEntry.COLUMN_LONGITUDE, -180.53);
        values.put(Contract.TruckConstantEntry.COLUMN_IS_DIRTY, false);
        values.put(Contract.TruckConstantEntry.COLUMN_COLOR_PRIMARY, "#000000");
        values.put(Contract.TruckConstantEntry.COLUMN_COLOR_SECONDARY, "#FFFFFF");

        Assertions.assertThat(db.insert(Contract.TruckConstantEntry.TABLE_NAME, null, values)).isEqualTo(1);
        return internalId;
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int internalCounter = 0;

        if (oldVersion <= 1) {
            // Load initial data
            onCreate(db);   // Works this time since it's version 1
            internalCounter++;
        }

        Assertions.assertThat(internalCounter).isEqualTo(newVersion);
    }
}
