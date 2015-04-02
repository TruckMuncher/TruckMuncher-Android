package com.truckmuncher.app.data.sql;

import android.database.Cursor;

import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class MenuViewTableTest extends DatabaseTableTestCase {

    @Test
    public void allExpectedColumnsExist() {
        String[] projection = new String[]{
                PublicContract.Menu._ID,
                PublicContract.Menu.MENU_ITEM_ID,
                PublicContract.Menu.MENU_ITEM_NAME,
                PublicContract.Menu.PRICE,
                PublicContract.Menu.MENU_ITEM_NOTES,
                PublicContract.Menu.ORDER_IN_CATEGORY,
                PublicContract.Menu.IS_AVAILABLE,
                PublicContract.Menu.CATEGORY_NAME,
                PublicContract.Menu.CATEGORY_ID,
                PublicContract.Menu.CATEGORY_NOTES,
                PublicContract.Menu.ORDER_IN_MENU,
                PublicContract.Menu.TRUCK_ID,
                PublicContract.Menu.MENU_ITEM_TAGS
        };
        Cursor cursor = db.query(Tables.MENU, projection, null, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isZero();
    }

    @Test
    public void tableHasSameNameAsInSqlScript() {
        assertThat(Tables.MENU).isEqualTo("menu");
    }
}
