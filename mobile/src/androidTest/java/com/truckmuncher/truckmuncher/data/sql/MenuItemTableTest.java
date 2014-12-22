package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.truckmuncher.truckmuncher.data.Contract;

import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.android.api.Assertions.assertThat;

public class MenuItemTableTest extends AndroidTestCase {

    private SqlOpenHelper helper;

    /**
     * Targets the latest version of the database schema.
     *
     * @return the internal id of the menu item
     */
    public static String onCreate(SQLiteDatabase db, String categoryId) {
        String internalId = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(Contract.MenuItem.COLUMN_INTERNAL_ID, internalId);
        values.put(Contract.MenuItem.COLUMN_NAME, "TestMenuItem1");
        values.put(Contract.MenuItem.COLUMN_PRICE, 14.99);
        values.put(Contract.MenuItem.COLUMN_IS_AVAILABLE, true);
        values.put(Contract.MenuItem.COLUMN_NOTES, "This is a test note for the first menu item.");
        values.put(Contract.MenuItem.COLUMN_TAGS, Contract.convertListToString(Arrays.asList("fish", "raw", "sandwich")));
        values.put(Contract.MenuItem.ORDER_IN_CATEGORY, 0);
        values.put(Contract.MenuItem.CATEGORY_ID, categoryId);
        Assertions.assertThat(db.insert(MenuItemTable.TABLE_NAME, null, values)).isEqualTo(1);

        internalId = UUID.randomUUID().toString();
        values = new ContentValues();
        values.put(Contract.MenuItem.COLUMN_INTERNAL_ID, internalId);
        values.put(Contract.MenuItem.COLUMN_NAME, "TestMenuItem2");
        values.put(Contract.MenuItem.COLUMN_PRICE, 5.99);
        values.put(Contract.MenuItem.COLUMN_IS_AVAILABLE, false);
        values.put(Contract.MenuItem.COLUMN_NOTES, "This is a test note for the second menu item.");
        values.put(Contract.MenuItem.COLUMN_TAGS, Contract.convertListToString(Arrays.asList("chicken")));
        values.put(Contract.MenuItem.ORDER_IN_CATEGORY, 1);
        values.put(Contract.MenuItem.CATEGORY_ID, categoryId);
        Assertions.assertThat(db.insert(MenuItemTable.TABLE_NAME, null, values)).isEqualTo(2);

        return internalId;
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = new SqlOpenHelperTest.InMemoryOpenHelper(getContext(), SqlOpenHelper.VERSION);
        SqlOpenHelperTest.populateTestData(helper.getWritableDatabase());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        helper.close();
    }

    public void testQueryMany() {
        SQLiteDatabase database = helper.getReadableDatabase();

        // Respects projection
        Cursor cursor = MenuItemTable.queryMany(database, Contract.MenuItem.CONTENT_URI, new String[]{Contract.MenuItem.COLUMN_NAME});
        assertThat(cursor)
                .hasColumnCount(1)
                .hasColumns(Contract.MenuItem.COLUMN_NAME)
                .hasCount(2);

        // Respects query params
        Uri uri = Contract.MenuItem.CONTENT_URI.buildUpon().appendQueryParameter(Contract.MenuItem.COLUMN_PRICE, "14.99").build();
        cursor = MenuItemTable.queryMany(database, uri, null);
        assertThat(cursor)
                .hasCount(1);
    }
}
