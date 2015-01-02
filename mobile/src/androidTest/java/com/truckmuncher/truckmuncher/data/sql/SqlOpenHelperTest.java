package com.truckmuncher.truckmuncher.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

public class SqlOpenHelperTest extends AndroidTestCase {

    private static final String TAG = SqlOpenHelperTest.class.getSimpleName();

    /**
     * This always targets the schema of the latest version of the database
     */
    public void testOnCreate() {
        SQLiteOpenHelper helper = new InMemoryOpenHelper(getContext(), 1);
        helper.getWritableDatabase();
        helper.close();
    }

    /**
     * This executes the full upgrade path. It starts by loading data using the version 1 schema, and migrates from there to the latest.
     */
    public void testOnUpgrade() {

        // We're not using Android's upgrade mechanism since we're forcing upgrades ourselves. Default to version 0.
        SQLiteOpenHelper helper = new InMemoryOpenHelper(getContext(), 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        // For each version, run the migrations, then apply test data
        for (int version = 1; version < SqlOpenHelper.VERSION; version++) {
            Log.d(TAG, "Running version " + version);
            helper.onUpgrade(db, version - 1, version);
        }

        // Clean up by closing out
        helper.close();
    }

    public static class InMemoryOpenHelper extends SqlOpenHelper {

        public InMemoryOpenHelper(Context context, int version) {
            super(context, null, version);
        }
    }
}
