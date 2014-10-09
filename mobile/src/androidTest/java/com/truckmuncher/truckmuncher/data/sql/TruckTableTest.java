package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.assertj.core.api.Assertions;

import java.util.UUID;

import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;

public class TruckTableTest {

    /**
     * Targets the latest version of the database schema.
     *
     * @return the internal id of the truck
     */
    public static String onCreate(SQLiteDatabase db) {
        String internalId = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(TruckEntry.COLUMN_INTERNAL_ID, internalId);
        values.put(TruckEntry.COLUMN_NAME, "TestTruck1");
        values.put(TruckEntry.COLUMN_IMAGE_URL, "http://api.truckmuncher.com/images/test");
        values.put(TruckEntry.COLUMN_KEYWORDS, "vegan, hot dog, lunch");

        Assertions.assertThat(db.insert(TruckEntry.TABLE_NAME, null, values)).isEqualTo(1);
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
