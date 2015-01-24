package com.truckmuncher.app.data.sql;

import android.content.ContentValues;
import android.database.Cursor;

import com.truckmuncher.app.data.PublicContract;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTableTest extends DatabaseTableTestCase {

    @Test
    public void allExpectedColumnsExist() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Category.ID, "hello");
        values.put(PublicContract.Category.NAME, "hello");
        values.put(PublicContract.Category.TRUCK_ID, "hello");
        values.put(PublicContract.Category.NOTES, "hello");
        values.put(PublicContract.Category.ORDER_IN_MENU, 2);
        assertThat(db.insert(Tables.CATEGORY, null, values)).isEqualTo(1);
    }

    @Test
    public void _idIsAutoIncrementPrimaryKey() {
        ContentValues values = new ContentValues();

        values.put(PublicContract.MenuItem.ID, 1234);
        assertThat(db.insert(Tables.CATEGORY, null, values)).isEqualTo(1);

        values.put(PublicContract.MenuItem.ID, 6789);
        assertThat(db.insert(Tables.CATEGORY, null, values)).isEqualTo(2);

        Cursor c = db.query(Tables.CATEGORY, new String[]{PublicContract.Category._ID}, null, null, null, null, null);

        assertThat(c.getCount()).isEqualTo(2);
        assertThat(c.moveToFirst()).isTrue();
        assertThat(c.getInt(0)).isEqualTo(1);

        assertThat(c.moveToNext()).isTrue();
        assertThat(c.getInt(0)).isEqualTo(2);
    }

    @Test
    public void idColumnIsUnique() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Category.ID, "hello");
        assertThat(db.insert(Tables.CATEGORY, null, values)).isEqualTo(1);

        // Can't do a repeat
        assertThat(db.insert(Tables.CATEGORY, null, values)).isEqualTo(-1);

        // Can do something different
        values.put(PublicContract.Category.ID, "world");
        assertThat(db.insert(Tables.CATEGORY, null, values)).isEqualTo(2);
    }

    @Test
    public void tableHasSameNameAsInSqlScript() {
        assertThat(Tables.CATEGORY).isEqualTo("category");
    }
}
