package com.truckmuncher.truckmuncher.data.sql;

import android.content.ContentValues;
import android.database.Cursor;

import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MenuItemTableTest extends DatabaseTableTestCase {

    @Test
    public void allExpectedColumnsExist() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.MenuItem.ID, "hello");
        values.put(PublicContract.MenuItem.NAME, "hello");
        values.put(PublicContract.MenuItem.PRICE, 3.99);
        values.put(PublicContract.MenuItem.IS_AVAILABLE, true);
        values.put(PublicContract.MenuItem.TAGS, "hello");
        values.put(PublicContract.MenuItem.CATEGORY_ID, "hello");
        values.put(PublicContract.MenuItem.NOTES, "hello");
        values.put(PublicContract.MenuItem.ORDER_IN_CATEGORY, 2);
        values.put(Contract.MenuItem.IS_DIRTY, true);
        assertThat(db.insert(Tables.MENU_ITEM, null, values)).isEqualTo(1);
    }

    @Test
    public void _idIsAutoIncrementPrimaryKey() {
        ContentValues values = new ContentValues();

        values.put(PublicContract.MenuItem.ID, 1234);
        assertThat(db.insert(Tables.MENU_ITEM, null, values)).isEqualTo(1);

        values.put(PublicContract.MenuItem.ID, 6789);
        assertThat(db.insert(Tables.MENU_ITEM, null, values)).isEqualTo(2);

        Cursor c = db.query(Tables.MENU_ITEM, new String[]{PublicContract.MenuItem._ID}, null, null, null, null, null);

        assertThat(c.getCount()).isEqualTo(2);
        assertThat(c.moveToFirst()).isTrue();
        assertThat(c.getInt(0)).isEqualTo(1);

        assertThat(c.moveToNext()).isTrue();
        assertThat(c.getInt(0)).isEqualTo(2);
    }

    @Test
    public void idColumnIsUnique() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.MenuItem.ID, "hello");
        assertThat(db.insert(Tables.MENU_ITEM, null, values)).isEqualTo(1);

        // Can't do a repeat
        assertThat(db.insert(Tables.MENU_ITEM, null, values)).isEqualTo(-1);

        // Can do something different
        values.put(PublicContract.MenuItem.ID, "world");
        assertThat(db.insert(Tables.MENU_ITEM, null, values)).isEqualTo(2);
    }

    @Test
    public void isDirtyDefaultsToFalse() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.MenuItem.ID, "hello");
        assertThat(db.insert(Tables.MENU_ITEM, null, values)).isEqualTo(1);

        Cursor cursor = db.query(Tables.MENU_ITEM, null, null, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.MenuItem.IS_DIRTY))).isZero();
    }

    @Test
    public void tableHasSameNameAsInSqlScript() {
        assertThat(Tables.MENU_ITEM).isEqualTo("menu_item");
    }
}
