package com.truckmuncher.truckmuncher.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

/**
 * Configures an in memory database with the latest migrations
 */
@RunWith(ReadableRobolectricTestRunner.class)
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

    /**
     * Creates an in-memory database that is configured the same way as the production one
     */
    private class TestOpenHelper extends SqlOpenHelper {
        public TestOpenHelper(Context context) {
            super(context, null, SqlOpenHelper.VERSION);
        }
    }
}
