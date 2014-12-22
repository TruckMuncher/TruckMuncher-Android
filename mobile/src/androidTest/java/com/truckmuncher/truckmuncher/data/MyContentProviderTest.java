package com.truckmuncher.truckmuncher.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.os.Handler;
import android.test.ProviderTestCase2;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class MyContentProviderTest extends ProviderTestCase2<MyContentProvider> {

    public MyContentProviderTest() {
        super(MyContentProvider.class, Contract.CONTENT_AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContentValues[] contentValues = new ContentValues[1];
        ContentValues values = new ContentValues();
        values.put(Contract.TruckConstantEntry.COLUMN_INTERNAL_ID, UUID.randomUUID().toString());
        contentValues[0] = values;
        getMockContentResolver().bulkInsert(Contract.TruckConstantEntry.CONTENT_URI, contentValues);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // Delete all entries
        getMockContentResolver().delete(Contract.TruckConstantEntry.CONTENT_URI, null, null);
    }

    public void testInsertIsNotSupported() {
        try {
            mContext.getContentResolver().insert(Contract.TruckConstantEntry.CONTENT_URI, null);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            // Just here to ensure this is tested in the future
        }
    }

    public void testUpdateRespectsNeedsSyncContract() {
        mContext.getContentResolver().registerContentObserver(Contract.TruckConstantEntry.CONTENT_URI, false, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
            }
        });
    }

    public void testUpdateRespectsNotifyContract() throws InterruptedException {

    }

    public void testBulkInsertRespectsNeedsSyncContract() {

    }

    public void testBulkInsertRespectsNotifyContract() {

    }

    public void testDeleteRespectsNeedsSyncContract() {

    }

    public void testDeleteRespectsNotifyContract() {

    }

    public void testGetType() {
        assertThat(mContext.getContentResolver().getType(Contract.TruckConstantEntry.CONTENT_URI))
                .isEqualTo(Contract.TruckConstantEntry.CONTENT_TYPE);

        assertThat(mContext.getContentResolver().getType(Contract.TruckConstantEntry.buildSingleTruck(UUID.randomUUID().toString())))
                .isEqualTo(Contract.TruckConstantEntry.CONTENT_ITEM_TYPE);

        assertThat(mContext.getContentResolver().getType(Contract.Category.CONTENT_URI))
                .isEqualTo(Contract.Category.CONTENT_TYPE);

        assertThat(mContext.getContentResolver().getType(Contract.MenuItem.CONTENT_URI))
                .isEqualTo(Contract.MenuItem.CONTENT_TYPE);
    }
}
