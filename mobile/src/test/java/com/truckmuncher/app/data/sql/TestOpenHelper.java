package com.truckmuncher.app.data.sql;

import android.content.Context;

/**
 * Creates an in-memory database that is configured the same way as the production one
 */
public class TestOpenHelper extends SqlOpenHelper {
    public TestOpenHelper(Context context) {
        super(context, null, SqlOpenHelper.VERSION);
    }
}
