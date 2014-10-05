package com.truckmuncher.truckmuncher.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

public class SqlOpenHelperTest extends AndroidTestCase {

    private SQLiteOpenHelper helper;

    public void testOnCreate() {
        helper = new InMemoryOpenHelper(getContext(), 1);
        helper.getWritableDatabase();
        helper.close();
    }

    private static class InMemoryOpenHelper extends SqlOpenHelper {

        private InMemoryOpenHelper(Context context, int version) {
            super(context, null, version);
        }
    }
}
