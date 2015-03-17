package com.truckmuncher.app.data.sql;

import android.content.ContentValues;
import android.database.Cursor;

import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class TruckPropertiesTableTest extends DatabaseTableTestCase {

    @Test
    public void allExpectedColumnsExist() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "hello");
        values.put(PublicContract.Truck.NAME, "hello");
        values.put(PublicContract.Truck.IMAGE_URL, "hello");
        values.put(PublicContract.Truck.KEYWORDS, "hello");
        values.put(PublicContract.Truck.COLOR_PRIMARY, "hello");
        values.put(PublicContract.Truck.COLOR_SECONDARY, "hello");
        values.put(PublicContract.Truck.DESCRIPTION, "hello");
        values.put(PublicContract.Truck.PHONE_NUMBER, "hello");
        assertThat(db.insert(Tables.TRUCK_PROPERTIES, null, values)).isEqualTo(1);
    }

    @Test
    public void _idIsAutoIncrementPrimaryKey() {
        ContentValues values = new ContentValues();

        values.put(PublicContract.Truck.ID, 1234);
        assertThat(db.insert(Tables.TRUCK_PROPERTIES, null, values)).isEqualTo(1);

        values.put(PublicContract.Truck.ID, 6789);
        assertThat(db.insert(Tables.TRUCK_PROPERTIES, null, values)).isEqualTo(2);

        Cursor c = db.query(Tables.TRUCK_PROPERTIES, new String[]{PublicContract.Truck._ID}, null, null, null, null, null);

        assertThat(c.getCount()).isEqualTo(2);
        assertThat(c.moveToFirst()).isTrue();
        assertThat(c.getInt(0)).isEqualTo(1);

        assertThat(c.moveToNext()).isTrue();
        assertThat(c.getInt(0)).isEqualTo(2);
    }

    @Test
    public void idColumnIsUnique() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "hello");
        assertThat(db.insert(Tables.TRUCK_PROPERTIES, null, values)).isEqualTo(1);

        // Can't do a repeat
        assertThat(db.insert(Tables.TRUCK_PROPERTIES, null, values)).isEqualTo(-1);

        // Can do something different
        values.put(PublicContract.Truck.ID, "world");
        assertThat(db.insert(Tables.TRUCK_PROPERTIES, null, values)).isEqualTo(2);
    }

    @Test
    public void tableHasSameNameAsInSqlScript() {
        assertThat(Tables.TRUCK_PROPERTIES).isEqualTo("truck_properties");
    }
}
