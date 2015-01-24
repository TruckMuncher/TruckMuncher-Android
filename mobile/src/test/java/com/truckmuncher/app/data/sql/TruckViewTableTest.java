package com.truckmuncher.app.data.sql;

import android.database.Cursor;

import com.truckmuncher.app.data.PublicContract;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TruckViewTableTest extends DatabaseTableTestCase {

    @Test
    public void allExpectedColumnsExist() {
        String[] projection = new String[]{
                PublicContract.Truck._ID,
                PublicContract.Truck.ID,
                PublicContract.Truck.NAME,
                PublicContract.Truck.IMAGE_URL,
                PublicContract.Truck.KEYWORDS,
                PublicContract.Truck.COLOR_PRIMARY,
                PublicContract.Truck.COLOR_SECONDARY,
                PublicContract.Truck.IS_SERVING,
                PublicContract.Truck.LATITUDE,
                PublicContract.Truck.LONGITUDE
        };
        Cursor cursor = db.query(Tables.TRUCK, projection, null, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isZero();
    }

    @Test
    public void tableHasSameNameAsInSqlScript() {
        assertThat(Tables.TRUCK).isEqualTo("truck");
    }
}
