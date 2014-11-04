package com.truckmuncher.truckmuncher.data.sync;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.RemoteException;
import android.test.InstrumentationTestCase;
import android.test.IsolatedContext;
import android.test.mock.MockContentResolver;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.wire.Wire;
import com.truckmuncher.api.trucks.ServingModeRequest;
import com.truckmuncher.api.trucks.ServingModeResponse;
import com.truckmuncher.truckmuncher.dagger.LocalNetworkModule;
import com.truckmuncher.truckmuncher.dagger.NetworkModule;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.test.asserts.Assertions;
import com.truckmuncher.truckmuncher.test.data.VerifiableContentProvider;

import java.io.IOException;
import java.util.UUID;

import dagger.ObjectGraph;

import static org.assertj.core.api.Assertions.assertThat;

public class SyncAdapterTest extends InstrumentationTestCase {

    SyncAdapter adapter;
    VerifiableContentProvider testProvider;
    Context testContext;
    MockWebServer testServer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Setup the mock context. This will fake our data persistence layer
        testProvider = new VerifiableContentProvider();
        MockContentResolver resolver = new MockContentResolver();
        resolver.addProvider(Contract.CONTENT_AUTHORITY, testProvider);
        testContext = new IsolatedContext(resolver, getInstrumentation().getTargetContext());

        // Setup the fake web server. We control the response and can assert the request
        // Using a fake server rather than a mocked interface means we test the full HTTP stack
        testServer = new MockWebServer();
        testServer.play();

        // Configure the SUT
        /*
         * Set the sync adapter as not syncable
         * Disallow parallel syncs
         */
        adapter = new SyncAdapter(testContext, false, false);
        ObjectGraph graph = ObjectGraph.create(new NetworkModule(testContext), new LocalNetworkModule(testServer));
        graph.inject(adapter);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testServer.shutdown();
    }

    public void testSyncTruckServingModeNoDirty() throws RemoteException {
        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @Override
            public Cursor onQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                return new MatrixCursor(projection);
            }
        });

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckEntry.CONTENT_URI);
        adapter.syncTruckServingMode(client);

        // If there are no results, the method should short circuit and no updates will happen.
        // If an update does happen, the test will fail.
        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }

    public void testSyncTruckServingModeHandlesNetworkFailure() {
        // TODO Create this test
    }

    public void testSyncTruckServingModeSuccess() throws RemoteException, IOException, InterruptedException {

        // Test data
        final String truckId = UUID.randomUUID().toString();
        final int isServing = 1;
        final double latitude = 43.1234;
        final double longitude = 87.1234;

        // Populate the data that should be synced by the adapter.
        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @Override
            public Cursor onQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(uri).isEqualTo(Contract.TruckEntry.buildDirty());

                MatrixCursor cursor = new MatrixCursor(projection);

                Object[] row = new Object[projection.length];
                row[SyncAdapter.TruckServingModeQuery.INTERNAL_ID] = truckId;
                row[SyncAdapter.TruckServingModeQuery.IS_SERVING] = isServing;
                row[SyncAdapter.TruckServingModeQuery.LATITUDE] = latitude;
                row[SyncAdapter.TruckServingModeQuery.LONGITUDE] = longitude;
                cursor.addRow(row);
                return cursor;
            }
        });

        // Make sure that the dirty flag is cleared
        testProvider.enqueue(new VerifiableContentProvider.UpdateEvent() {
            @Override
            public int onUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                assertThat(uri).isEqualTo(Contract.buildSuppressNotify(Contract.TruckEntry.buildSingleTruck(truckId)));
                assertThat(values.getAsBoolean(Contract.TruckEntry.COLUMN_IS_DIRTY)).isFalse();
                return 1;
            }
        });

        // Setup the web server with the network calls we are expecting.
        MockResponse response = new MockResponse()
                .setBody(new ServingModeResponse().toByteArray())
                .setHeader("Content-Type", "application/x-protobuf");
        testServer.enqueue(response);

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckEntry.CONTENT_URI);
        adapter.syncTruckServingMode(client);

        // Confirm the request was as expected
        RecordedRequest request = testServer.takeRequest();
        Assertions.assertThat(request)
                .hasNonceHeader()
                .hasTimestampHeader();

        ServingModeRequest receivedRequest = new Wire().parseFrom(request.getBody(), ServingModeRequest.class);
        assertThat(receivedRequest.truckId).isEqualTo(truckId);
        assertThat(receivedRequest.isInServingMode).isTrue();
        assertThat(receivedRequest.truckLatitude).isEqualTo(latitude);
        assertThat(receivedRequest.truckLongitude).isEqualTo(longitude);

        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }
}
