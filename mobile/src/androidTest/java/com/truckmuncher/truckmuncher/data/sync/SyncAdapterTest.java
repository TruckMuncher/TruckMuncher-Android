package com.truckmuncher.truckmuncher.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.test.InstrumentationTestCase;
import android.test.IsolatedContext;
import android.test.mock.MockContentResolver;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.wire.Wire;
import com.truckmuncher.api.auth.AuthResponse;
import com.truckmuncher.api.exceptions.Error;
import com.truckmuncher.api.menu.MenuItemAvailability;
import com.truckmuncher.api.menu.ModifyMenuItemAvailabilityRequest;
import com.truckmuncher.api.menu.ModifyMenuItemAvailabilityResponse;
import com.truckmuncher.api.trucks.ServingModeRequest;
import com.truckmuncher.api.trucks.ServingModeResponse;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.dagger.LocalNetworkModule;
import com.truckmuncher.truckmuncher.dagger.NetworkModule;
import com.truckmuncher.truckmuncher.dagger.TestUserModule;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.test.asserts.Assertions;
import com.truckmuncher.truckmuncher.test.data.VerifiableContentProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import dagger.ObjectGraph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        AccountManager accountManager = mock(AccountManager.class);
        when(accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE)).thenReturn(new Account[]{TestUserModule.ACCOUNT});
        ObjectGraph graph = ObjectGraph.create(new NetworkModule(testContext), new LocalNetworkModule(testContext, testServer), new TestUserModule(accountManager));
        graph.inject(adapter);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testServer.shutdown();
    }

    public void testSyncMenuItemAvailabilityNoDirtyRecords() throws RemoteException {
        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                return new MatrixCursor(projection);
            }
        });

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        adapter.syncMenuItemAvailability(client, new SyncResult());

        // If there are no results, the method should short circuit and no updates will happen.
        // If an update does happen, the test will fail.
        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }

    public void testSyncMenuItemAvailabilitySuccess() throws RemoteException, InterruptedException, IOException {

        // Test values
        final String menuItemId1 = UUID.randomUUID().toString();
        final String menuItemId2 = UUID.randomUUID().toString();

        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(uri).isEqualTo(Contract.MenuItemEntry.buildDirty());

                MatrixCursor cursor = new MatrixCursor(projection);

                Object[] row1 = new Object[projection.length];
                row1[SyncAdapter.MenuItemAvailabilityQuery.INTERNAL_ID] = menuItemId1;
                row1[SyncAdapter.MenuItemAvailabilityQuery.IS_AVAILABLE] = 1;
                cursor.addRow(row1);

                // Return 2 rows so that the while loop of diffs in the SyncAdapter is exercised
                Object[] row2 = new Object[projection.length];
                row2[SyncAdapter.MenuItemAvailabilityQuery.INTERNAL_ID] = menuItemId2;
                row2[SyncAdapter.MenuItemAvailabilityQuery.IS_AVAILABLE] = 1;
                cursor.addRow(row2);

                return cursor;
            }
        });

        // Make sure that the dirty flags are cleared but only on those items that were updated
        testProvider.enqueue(new VerifiableContentProvider.BulkInsertEvent() {
            @Override
            public int onBulkInsert(Uri uri, @NonNull ContentValues[] values) {
                assertThat(uri).isEqualTo(Contract.buildSuppressNotify(Contract.MenuItemEntry.CONTENT_URI));

                ContentValues row1 = new ContentValues(2);
                row1.put(Contract.MenuItemEntry.COLUMN_INTERNAL_ID, menuItemId1);
                row1.put(Contract.MenuItemEntry.COLUMN_IS_DIRTY, false);
                ContentValues row2 = new ContentValues(2);
                row2.put(Contract.MenuItemEntry.COLUMN_INTERNAL_ID, menuItemId2);
                row2.put(Contract.MenuItemEntry.COLUMN_IS_DIRTY, false);

                assertThat(values)
                        .hasSize(2)
                        .containsExactly(row1, row2);
                return 2;
            }
        });

        // Setup the web server with the network calls we are expecting.
        MockResponse response = new MockResponse()
                .setBody(new ModifyMenuItemAvailabilityResponse().toByteArray())
                .setHeader("Content-Type", "application/x-protobuf");
        testServer.enqueue(response);

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        adapter.syncMenuItemAvailability(client, new SyncResult());

        // Confirm the request was as expected
        RecordedRequest request = testServer.takeRequest();
        Assertions.assertThat(request)
                .hasNonceHeader()
                .hasTimestampHeader();

        ModifyMenuItemAvailabilityRequest receivedRequest = new Wire().parseFrom(request.getBody(), ModifyMenuItemAvailabilityRequest.class);
        List<MenuItemAvailability> expectedDiff = Arrays.asList(
                new MenuItemAvailability(menuItemId1, true),
                new MenuItemAvailability(menuItemId2, true)
        );
        assertThat(receivedRequest.diff).isEqualTo(expectedDiff);

        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }

    public void testSyncMenuItemAvailabilityNetworkFailure() throws RemoteException {

        // Test values
        final String menuItemId = UUID.randomUUID().toString();

        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(uri).isEqualTo(Contract.MenuItemEntry.buildDirty());

                MatrixCursor cursor = new MatrixCursor(projection);

                Object[] row = new Object[projection.length];
                row[SyncAdapter.MenuItemAvailabilityQuery.INTERNAL_ID] = menuItemId;
                row[SyncAdapter.MenuItemAvailabilityQuery.IS_AVAILABLE] = 1;
                cursor.addRow(row);

                return cursor;
            }
        });

        // Setup the web server with the network calls we are expecting.
        MockResponse errorResponse = new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", "application/x-protobuf")
                .setBody(new Error("1234", "Mock message").toByteArray());
        testServer.enqueue(errorResponse);

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        adapter.syncMenuItemAvailability(client, new SyncResult());

        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }

    public void testSyncMenuItemAvailabilityExpiredSession() throws RemoteException {

        // Test values
        final String menuItemId = UUID.randomUUID().toString();

        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(uri).isEqualTo(Contract.MenuItemEntry.buildDirty());

                MatrixCursor cursor = new MatrixCursor(projection);

                Object[] row = new Object[projection.length];
                row[SyncAdapter.MenuItemAvailabilityQuery.INTERNAL_ID] = menuItemId;
                row[SyncAdapter.MenuItemAvailabilityQuery.IS_AVAILABLE] = 1;
                cursor.addRow(row);

                return cursor;
            }
        });

        // Setup the web server with the network calls we are expecting.
        MockResponse errorResponse = new MockResponse()
                .setResponseCode(401)
                .setHeader("Content-Type", "application/x-protobuf")
                .setBody(new Error("1234", "Expired session").toByteArray());
        testServer.enqueue(errorResponse);
        MockResponse authResponse = new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/x-protobuf")
                .setBody(new AuthResponse("", "", UUID.randomUUID().toString()).toByteArray());
        testServer.enqueue(authResponse);

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        SyncResult result = new SyncResult();
        adapter.syncMenuItemAvailability(client, result);

        assertThat(result.fullSyncRequested).isTrue();

        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }

    public void testSyncMenuItemAvailabilityInvalidSocialCreds() throws RemoteException {

        // Test values
        final String menuItemId = UUID.randomUUID().toString();

        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(uri).isEqualTo(Contract.MenuItemEntry.buildDirty());

                MatrixCursor cursor = new MatrixCursor(projection);

                Object[] row = new Object[projection.length];
                row[SyncAdapter.MenuItemAvailabilityQuery.INTERNAL_ID] = menuItemId;
                row[SyncAdapter.MenuItemAvailabilityQuery.IS_AVAILABLE] = 1;
                cursor.addRow(row);

                return cursor;
            }
        });

        // Setup the web server with the network calls we are expecting.
        MockResponse errorResponse = new MockResponse()
                .setResponseCode(401)
                .setHeader("Content-Type", "application/x-protobuf")
                .setBody(new Error("1234", "Expired session").toByteArray());
        testServer.enqueue(errorResponse);

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        SyncResult result = new SyncResult();
        adapter.syncMenuItemAvailability(client, result);

        assertThat(result.tooManyRetries).isTrue();

        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }

    public void testSyncTruckServingModeNoDirtyRecords() throws RemoteException {
        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                return new MatrixCursor(projection);
            }
        });

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        adapter.syncTruckServingMode(client, new SyncResult());

        // If there are no results, the method should short circuit and no updates will happen.
        // If an update does happen, the test will fail.
        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }

    public void testSyncTruckServingModeSuccess() throws RemoteException, IOException, InterruptedException {

        // Test values
        final String truckId = UUID.randomUUID().toString();
        final int isServing = 1;
        final double latitude = 43.1234;
        final double longitude = 87.1234;

        // Populate the data that should be synced by the adapter.
        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(uri).isEqualTo(Contract.TruckConstantEntry.buildDirty());

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
                assertThat(uri).isEqualTo(Contract.buildSuppressNotify(Contract.TruckConstantEntry.buildSingleTruck(truckId)));
                assertThat(values.getAsBoolean(Contract.TruckConstantEntry.COLUMN_IS_DIRTY)).isFalse();
                return 1;
            }
        });

        // Setup the web server with the network calls we are expecting.
        MockResponse response = new MockResponse()
                .setBody(new ServingModeResponse().toByteArray())
                .setHeader("Content-Type", "application/x-protobuf");
        testServer.enqueue(response);

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        adapter.syncTruckServingMode(client, new SyncResult());

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

    public void testSyncTruckServingModeHandlesNetworkFailure() throws RemoteException {

        // Test values
        final String truckId1 = UUID.randomUUID().toString();
        final String truckId2 = UUID.randomUUID().toString();
        final int isServing = 1;
        final double latitude = 43.1234;
        final double longitude = 87.1234;

        // Populate the data that should be synced by the adapter
        testProvider.enqueue(new VerifiableContentProvider.QueryEvent() {
            @NonNull
            @Override
            public Cursor onQuery(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                assertThat(uri).isEqualTo(Contract.TruckConstantEntry.buildDirty());

                MatrixCursor cursor = new MatrixCursor(projection);
                Object[] row1 = new Object[projection.length];
                row1[SyncAdapter.TruckServingModeQuery.INTERNAL_ID] = truckId1;
                row1[SyncAdapter.TruckServingModeQuery.IS_SERVING] = isServing;
                row1[SyncAdapter.TruckServingModeQuery.LATITUDE] = latitude;
                row1[SyncAdapter.TruckServingModeQuery.LONGITUDE] = longitude;
                cursor.addRow(row1);

                Object[] row2 = new Object[projection.length];
                row2[SyncAdapter.TruckServingModeQuery.INTERNAL_ID] = truckId2;
                row2[SyncAdapter.TruckServingModeQuery.IS_SERVING] = isServing;
                row2[SyncAdapter.TruckServingModeQuery.LATITUDE] = latitude;
                row2[SyncAdapter.TruckServingModeQuery.LONGITUDE] = longitude;
                cursor.addRow(row2);

                return cursor;
            }
        });

        // The first request will fail. So only the second should have the dirty flag cleared
        testProvider.enqueue(new VerifiableContentProvider.UpdateEvent() {
            @Override
            public int onUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                assertThat(uri).isEqualTo(Contract.buildSuppressNotify(Contract.TruckConstantEntry.buildSingleTruck(truckId2)));
                assertThat(values.getAsBoolean(Contract.TruckConstantEntry.COLUMN_IS_DIRTY)).isFalse();
                return 1;
            }
        });

        // Setup the web server with the network calls we are expecting.
        MockResponse errorResponse = new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", "application/x-protobuf")
                .setBody(new Error("1234", "Mock message").toByteArray());
        MockResponse mockResponse = new MockResponse()
                .setBody(new ServingModeResponse().toByteArray())
                .setHeader("Content-Type", "application/x-protobuf");
        testServer.enqueue(errorResponse);
        testServer.enqueue(mockResponse);

        // Run the sync manually
        ContentProviderClient client = testContext.getContentResolver().acquireContentProviderClient(Contract.TruckConstantEntry.CONTENT_URI);
        adapter.syncTruckServingMode(client, new SyncResult());

        testProvider.assertThatQueuesAreEmpty();
        testProvider.assertThatCursorsAreClosed();
    }
}
