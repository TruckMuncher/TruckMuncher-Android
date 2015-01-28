package com.truckmuncher.app.data.sql;

import android.content.ContentValues;
import android.database.Cursor;

import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TruckStateTableTest extends DatabaseTableTestCase {

    @Test
    public void allExpectedColumnsExist() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "hello");
        values.put(PublicContract.Truck.IS_SERVING, true);
        values.put(PublicContract.Truck.LATITUDE, 43.99);
        values.put(PublicContract.Truck.LONGITUDE, 53.99);
        values.put(Contract.TruckState.IS_DIRTY, true);
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(1);
    }

    @Test
    public void _idIsAutoIncrementPrimaryKey() {
        ContentValues values = new ContentValues();

        values.put(PublicContract.Truck.ID, 1234);
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(1);

        values.put(PublicContract.Truck.ID, 6789);
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(2);

        Cursor c = db.query(Tables.TRUCK_STATE, new String[]{PublicContract.Truck._ID}, null, null, null, null, null);

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
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(1);

        // Can't do a repeat
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(-1);

        // Can do something different
        values.put(PublicContract.Truck.ID, "world");
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(2);
    }

    @Test
    public void isDirtyDefaultsToFalse() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "hello");
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(1);

        Cursor cursor = db.query(Tables.TRUCK_STATE, null, null, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.TruckState.IS_DIRTY))).isZero();
    }

    @Test
    public void isServingDefaultsToFalse() {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "hello");
        assertThat(db.insert(Tables.TRUCK_STATE, null, values)).isEqualTo(1);

        Cursor cursor = db.query(Tables.TRUCK_STATE, null, null, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isTrue();
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(PublicContract.Truck.IS_SERVING))).isZero();
    }

    @Test
    public void tableHasSameNameAsInSqlScript() {
        assertThat(Tables.TRUCK_STATE).isEqualTo("truck_state");
    }
}
