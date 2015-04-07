package com.truckmuncher.app.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.app.data.sql.SqlOpenHelper;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sql.WhereClause;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class TruckMuncherContentProviderTest {

    private ContentResolver resolver;

    @Before
    public void setUp() {
        ContentProvider provider = new TruckMuncherContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider(PublicContract.CONTENT_AUTHORITY, provider);
        resolver = Robolectric.application.getContentResolver();
    }

    @Test
    public void queryCategoryHitsCategoryTable() {

        // Populate some data
        ContentValues values = new ContentValues();
        values.put(PublicContract.Category.NAME, "Sandwiches");
        SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase().insert(Tables.CATEGORY, null, values);

        // Make sure we got our data
        Cursor cursor = resolver.query(PublicContract.CATEGORY_URI, new String[]{PublicContract.Category.NAME}, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);     // Verifies the behavior because the rest of the database is empty
        assertThat(cursor.getColumnCount()).isEqualTo(1);   // Verifies that the projection is respected

        // Make sure selection is respected
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Category.NAME, EQUALS, "Soups")
                .build();
        cursor = resolver.query(PublicContract.CATEGORY_URI, null, whereClause.selection, whereClause.selectionArgs, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isZero();
    }

    @Test
    public void queryMenuItemHitsMenuItemTable() {

        // Populate some data
        ContentValues values = new ContentValues();
        values.put(PublicContract.MenuItem.NAME, "BLT");
        SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase().insert(Tables.MENU_ITEM, null, values);

        // Make sure we got our data
        Cursor cursor = resolver.query(PublicContract.MENU_ITEM_URI, new String[]{PublicContract.MenuItem.NAME}, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);     // Verifies the behavior because the rest of the database is empty
        assertThat(cursor.getColumnCount()).isEqualTo(1);   // Verifies that the projection is respected

        // Make sure selection is respected
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.MenuItem.NAME, EQUALS, "Turkey")
                .build();
        cursor = resolver.query(PublicContract.MENU_ITEM_URI, null, whereClause.selection, whereClause.selectionArgs, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isZero();
    }

    @Test
    public void queryTruckHitsTruckView() {
        SQLiteDatabase db = SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase();

        // Populate some data
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "Truck_1");
        values.put(PublicContract.Truck.NAME, "The Sandwich Makers");
        db.insert(Tables.TRUCK_PROPERTIES, null, values);

        ContentValues stateValues = new ContentValues();
        stateValues.put(PublicContract.Truck.ID, "Truck_1");
        stateValues.put(PublicContract.Truck.IS_SERVING, true);
        db.insert(Tables.TRUCK_STATE, null, stateValues);

        // Make sure we got our data
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.NAME, EQUALS, "The Sandwich Makers")
                .where(PublicContract.Truck.IS_SERVING, EQUALS, true)
                .build();
        String[] projection = new String[]{PublicContract.Truck.NAME};
        Cursor cursor = resolver.query(PublicContract.TRUCK_URI, projection, whereClause.selection, whereClause.selectionArgs, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);     // Verifies the behavior because only the view has both of those columns
        assertThat(cursor.getColumnCount()).isEqualTo(1);   // Verifies that the projection is respected
    }

    @Test
    public void queryTruckStateHitsTruckStateTable() {

        // Populate some data
        ContentValues values = new ContentValues();
        values.put(Contract.TruckState.IS_DIRTY, true);
        SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase().insert(Tables.TRUCK_STATE, null, values);

        // Make sure we got our data
        Cursor cursor = resolver.query(Contract.TRUCK_STATE_URI, new String[]{Contract.TruckState.IS_DIRTY}, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);     // Verifies the behavior because the rest of the database is empty
        assertThat(cursor.getColumnCount()).isEqualTo(1);   // Verifies that the projection is respected

        // Make sure selection is respected
        WhereClause whereClause = new WhereClause.Builder()
                .where(Contract.TruckState.IS_DIRTY, EQUALS, false)
                .build();
        cursor = resolver.query(Contract.TRUCK_STATE_URI, null, whereClause.selection, whereClause.selectionArgs, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isZero();
    }

    @Test
    public void queryMenuHitsMenuView() {
        SQLiteDatabase db = SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase();

        // Populate some data
        ContentValues truckValues = new ContentValues();
        truckValues.put(PublicContract.Truck.ID, "Truck_1");
        db.insert(Tables.TRUCK_PROPERTIES, null, truckValues);

        ContentValues categoryValues = new ContentValues();
        categoryValues.put(PublicContract.Category.NAME, "Sandwiches");
        categoryValues.put(PublicContract.Category.ID, "Category_1");
        categoryValues.put(PublicContract.Category.TRUCK_ID, "Truck_1");
        db.insert(Tables.CATEGORY, null, categoryValues);

        ContentValues itemValues = new ContentValues();
        itemValues.put(PublicContract.MenuItem.NAME, "BLT");
        itemValues.put(PublicContract.MenuItem.CATEGORY_ID, "Category_1");
        db.insert(Tables.MENU_ITEM, null, itemValues);

        // Make sure we got our data
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Menu.CATEGORY_NAME, EQUALS, "Sandwiches")
                .where(PublicContract.Menu.MENU_ITEM_NAME, EQUALS, "BLT")
                .build();
        String[] projection = new String[]{PublicContract.Menu.CATEGORY_NAME};
        Cursor cursor = resolver.query(PublicContract.MENU_URI, projection, whereClause.selection, whereClause.selectionArgs, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);     // Verifies the behavior because only the view has both of those columns
        assertThat(cursor.getColumnCount()).isEqualTo(1);   // Verifies that the projection is respected
    }

    @Test
    public void getTypeWorksForKnownUris() {
        String type;

        // Category
        type = resolver.getType(PublicContract.CATEGORY_URI);
        assertThat(type).isEqualTo(PublicContract.URI_TYPE_CATEGORY);

        // Menu Item
        type = resolver.getType(PublicContract.MENU_ITEM_URI);
        assertThat(type).isEqualTo(PublicContract.URI_TYPE_MENU_ITEM);

        // Truck
        type = resolver.getType(PublicContract.TRUCK_URI);
        assertThat(type).isEqualTo(PublicContract.URI_TYPE_TRUCK);

        // Menu
        type = resolver.getType(PublicContract.MENU_URI);
        assertThat(type).isEqualTo(PublicContract.URI_TYPE_MENU);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertThrowsUnsupportedOperationException() {
        resolver.insert(PublicContract.CATEGORY_URI, null);
    }

    @Test
    public void deleteTruckStateHitsTruckStateTable() {

        // Populate some data
        queryTruckStateHitsTruckStateTable();

        // Make sure the selection is respected
        WhereClause whereClause = new WhereClause.Builder()
                .where(Contract.TruckState.IS_DIRTY, EQUALS, false)
                .build();
        int deletedCount = resolver.delete(Contract.TRUCK_STATE_URI, whereClause.selection, whereClause.selectionArgs);
        assertThat(deletedCount).isZero();

        // Make sure it gets deleted
        whereClause = new WhereClause.Builder()
                .where(Contract.TruckState.IS_DIRTY, EQUALS, true)
                .build();
        deletedCount = resolver.delete(Contract.TRUCK_STATE_URI, whereClause.selection, whereClause.selectionArgs);
        assertThat(deletedCount).isEqualTo(1);
    }

    @Test
    public void updateTruckStateHitsTruckStateTable() {

        // Populate some data
        queryTruckStateHitsTruckStateTable();

        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "The Sandwich Makers");

        // Make sure the selection is respected
        WhereClause whereClause = new WhereClause.Builder()
                .where(Contract.TruckState.IS_DIRTY, EQUALS, false)
                .build();

        int updatedCount = resolver.update(Contract.TRUCK_STATE_URI, values, whereClause.selection, whereClause.selectionArgs);
        assertThat(updatedCount).isZero();

        // Make sure the right table is hit
        updatedCount = resolver.update(Contract.TRUCK_STATE_URI, values, null, null);
        assertThat(updatedCount).isEqualTo(1);
    }

    @Test
    public void bulkInsertMenuItemAddsNewData() {
        ContentValues blt = new ContentValues();
        blt.put(PublicContract.MenuItem.ID, "BLT");
        ContentValues turkey = new ContentValues();
        turkey.put(PublicContract.MenuItem.ID, "Turkey");
        ContentValues[] valuesList = new ContentValues[]{blt, turkey};

        int inserted = resolver.bulkInsert(PublicContract.MENU_ITEM_URI, valuesList);
        assertThat(inserted).isEqualTo(2);
    }

    @Test
    public void bulkInsertMenuItemUpdatesOldData() {

        // Add some "existing" mock data
        ContentValues existing = new ContentValues();
        existing.put(PublicContract.MenuItem.ID, "BLT");
        existing.put(PublicContract.MenuItem.PRICE, 5.99);
        assertThat(SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase().insert(Tables.MENU_ITEM, null, existing)).isEqualTo(1);

        // Update the mock data
        ContentValues updated = new ContentValues();
        updated.put(PublicContract.MenuItem.ID, "BLT");
        updated.put(PublicContract.MenuItem.PRICE, 6.99);
        int inserted = resolver.bulkInsert(PublicContract.MENU_ITEM_URI, new ContentValues[]{updated});
        assertThat(inserted).isEqualTo(1);

        // Verify that the mock data indeed exists
        Cursor cursor = resolver.query(PublicContract.MENU_ITEM_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);
        cursor.moveToFirst();
        assertThat(cursor.getString(cursor.getColumnIndex(PublicContract.MenuItem.ID))).isEqualTo("BLT");
        assertThat(cursor.getDouble(cursor.getColumnIndex(PublicContract.MenuItem.PRICE))).isEqualTo(6.99);
    }

    @Test
    public void bulkInsertCategoryAddsNewData() {
        ContentValues sandwiches = new ContentValues();
        sandwiches.put(PublicContract.Category.ID, "Sandwiches");
        ContentValues soups = new ContentValues();
        soups.put(PublicContract.Category.ID, "Soups");
        ContentValues[] valuesList = new ContentValues[]{sandwiches, soups};

        int inserted = resolver.bulkInsert(PublicContract.CATEGORY_URI, valuesList);
        assertThat(inserted).isEqualTo(2);
    }

    @Test
    public void bulkInsertCategoryUpdatesOldData() {

        // Add some "existing" mock data
        ContentValues existing = new ContentValues();
        existing.put(PublicContract.Category.ID, "Sandwiches");
        existing.put(PublicContract.Category.ORDER_IN_MENU, 1);
        assertThat(SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase().insert(Tables.CATEGORY, null, existing)).isEqualTo(1);

        // Update the mock data
        ContentValues updated = new ContentValues();
        updated.put(PublicContract.Category.ID, "Sandwiches");
        updated.put(PublicContract.Category.ORDER_IN_MENU, 2);
        int inserted = resolver.bulkInsert(PublicContract.CATEGORY_URI, new ContentValues[]{updated});
        assertThat(inserted).isEqualTo(1);

        // Verify that the mock data indeed exists
        Cursor cursor = resolver.query(PublicContract.CATEGORY_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);
        cursor.moveToFirst();
        assertThat(cursor.getString(cursor.getColumnIndex(PublicContract.Category.ID))).isEqualTo("Sandwiches");
        assertThat(cursor.getInt(cursor.getColumnIndex(PublicContract.Category.ORDER_IN_MENU))).isEqualTo(2);
    }

    @Test
    public void bulkInsertTruckStateAddsNewData() {
        ContentValues sandwich = new ContentValues();
        sandwich.put(PublicContract.Truck.ID, "The Sandwich Makers");
        ContentValues soup = new ContentValues();
        soup.put(PublicContract.Truck.ID, "The Soup Makers");
        ContentValues[] valuesList = new ContentValues[]{sandwich, soup};

        int inserted = resolver.bulkInsert(Contract.TRUCK_STATE_URI, valuesList);
        assertThat(inserted).isEqualTo(2);
    }

    @Test
    public void bulkInsertTruckStateUpdatesOldData() {

        // Add some "existing" mock data
        ContentValues existing = new ContentValues();
        existing.put(PublicContract.Truck.IS_SERVING, true);
        existing.put(PublicContract.Truck.ID, "The Sandwich Makers");
        assertThat(SqlOpenHelper.newInstance(Robolectric.application).getWritableDatabase().insert(Tables.TRUCK_STATE, null, existing)).isEqualTo(1);

        // Update the mock data
        ContentValues updated = new ContentValues();
        updated.put(PublicContract.Truck.ID, "The Sandwich Makers");
        updated.put(PublicContract.Truck.IS_SERVING, false);
        int inserted = resolver.bulkInsert(Contract.TRUCK_STATE_URI, new ContentValues[]{updated});
        assertThat(inserted).isEqualTo(1);

        // Verify that the mock data indeed exists
        Cursor cursor = resolver.query(Contract.TRUCK_STATE_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);
        cursor.moveToFirst();
        assertThat(cursor.getString(cursor.getColumnIndex(PublicContract.Truck.ID))).isEqualTo("The Sandwich Makers");
        assertThat(cursor.getInt(cursor.getColumnIndex(PublicContract.Truck.IS_SERVING)) == 1).isFalse();
    }
}
