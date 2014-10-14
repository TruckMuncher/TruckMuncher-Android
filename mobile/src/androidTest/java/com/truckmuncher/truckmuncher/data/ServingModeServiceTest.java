package com.truckmuncher.truckmuncher.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.test.ServiceTestCase;
import android.test.mock.MockContentResolver;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.truckmuncher.truckmuncher.test.data.VerifiableContentProvider;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ServingModeServiceTest extends ServiceTestCase<ServingModeServiceTest.ServingModeServiceWrapper> {

    private CountDownLatch latch;
    private VerifiableContentProvider contentProvider;

    public ServingModeServiceTest() {
        super(ServingModeServiceTest.ServingModeServiceWrapper.class);
    }

    @Override
    protected void setupService() {
        super.setupService();

        latch = new CountDownLatch(1);
        getService().latch = latch;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        contentProvider = new VerifiableContentProvider();
        final MockContentResolver mockContentResolver = new MockContentResolver();
        mockContentResolver.addProvider(getContext().getPackageName(), contentProvider);
        Context context = new ContextWrapper(getContext()) {
            @Override
            public ContentResolver getContentResolver() {
                return mockContentResolver;
            }
        };
        setContext(context);
    }

    public void testNullDataDoesntQueryProvider() throws InterruptedException {

        Intent intent = new Intent(getContext(), ServingModeServiceWrapper.class);
        startService(intent);

        latch.await(5, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isEqualTo(0);
    }

    public void testEmptyCursorDoesntHitServer() throws InterruptedException {

        // Setup expectations
        contentProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @Override
            public Cursor onQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                return new MatrixCursor(ServingModeService.TruckQuery.PROJECTION);
            }
        });

        Intent intent = new Intent(getContext(), ServingModeServiceWrapper.class);
        intent.setData(Contract.TruckEntry.buildSingleTruck(UUID.randomUUID().toString()));

        startService(intent);

        latch.await(5, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isEqualTo(0);

        // TODO setup mock server and ensure it doesnt get hit
    }

    public void testSuccessfulUpdate() throws InterruptedException, IOException {

        // Setup expectations
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse());
        server.play();
        final String internalId = UUID.randomUUID().toString();
        contentProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @Override
            public Cursor onQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(Contract.TruckEntry.getInternalIdFromUri(uri)).isEqualTo(internalId);
                MatrixCursor cursor = new MatrixCursor(ServingModeService.TruckQuery.PROJECTION);
                cursor.addRow(new Object[]{internalId, 1, 88.2, 94.5});
                return cursor;
            }
        });

        Intent intent = new Intent(getContext(), ServingModeServiceWrapper.class);
        intent.setData(Contract.TruckEntry.buildSingleTruck(internalId));
        startService(intent);

        latch.await(5, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isEqualTo(0);
        contentProvider.verify();

        // TODO setup mock server and ensure it gets hit
    }

    public static class ServingModeServiceWrapper extends ServingModeService {

        private CountDownLatch latch;

        @Override
        protected void onHandleIntent(Intent intent) {
            super.onHandleIntent(intent);
            latch.countDown();
        }
    }
}
