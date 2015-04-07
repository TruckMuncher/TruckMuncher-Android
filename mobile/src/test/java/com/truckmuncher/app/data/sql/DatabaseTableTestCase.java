package com.truckmuncher.app.data.sql;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;

/**
 * Configures an in memory database with the latest migrations
 */
public class DatabaseTableTestCase {

    TestOpenHelper openHelper;
    SQLiteDatabase db;

    @Before
    public void setUp() {
        openHelper = new TestOpenHelper(Robolectric.application);
        db = openHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        openHelper.close();
    }

}
