package com.truckmuncher.app.vendor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.otto.Bus;
import com.squareup.wire.Wire;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.data.ApiException;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sql.WhereClause;
import com.truckmuncher.app.data.sync.ApiExceptionResolver;
import com.truckmuncher.app.data.sync.ApiResult;

import javax.inject.Inject;

import timber.log.Timber;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class VendorTruckStateResolver {

    private final TruckService truckService;
    private final ApiExceptionResolver exceptionResolver;
    private final SQLiteDatabase database;
    private final UserAccount userAccount;
    private final ContentResolver contentResolver;
    private final Bus bus;

    @Inject
    public VendorTruckStateResolver(TruckService truckService, ApiExceptionResolver exceptionResolver, SQLiteDatabase database, UserAccount userAccount, ContentResolver contentResolver, Bus bus) {
        this.truckService = truckService;
        this.exceptionResolver = exceptionResolver;
        this.database = database;
        this.userAccount = userAccount;
        this.contentResolver = contentResolver;
        this.bus = bus;
    }

    public void resolveState() {

        TrucksForVendorResponse response;
        try {
            response = truckService.getTrucksForVendor(new TrucksForVendorRequest());
        } catch (ApiException e) {
            ApiResult result = exceptionResolver.resolve(e);
            switch (result) {
                case SHOULD_RETRY:  // Fall through
                case TEMPORARY_ERROR:
                    resolveState();   // Retry
                    return;
                case PERMANENT_ERROR:  // Fall through
                case NEEDS_USER_INPUT:
                    Timber.e(e, "Got an error while getting trucks for vendor.");
                    // TODO need to communicate this to the UI somehow
                    return;
                default:
                    throw new IllegalStateException("Not expecting this result", e);
            }
        }

        if (Wire.get(response.isNew, TrucksForVendorResponse.DEFAULT_ISNEW)) {

            // A new truck is the equivalent of not having one. Let the logic where we fetch all trucks handle this case.
            return;
        }

        boolean wasSuccessful = false;
        try {
            database.beginTransaction();

            // We need to clear the owner of all trucks since the owner might have changed.
            // Only then can we assign the current user as the owner.
            ContentValues ownerColumn = new ContentValues(1);
            ownerColumn.put(PublicContract.Truck.OWNER_ID, (String) null);
            database.update(Tables.TRUCK_PROPERTIES, ownerColumn, null, null);

            // Now that we have valid trucks that belong to the current user, assign them to the user. Other parts of the system
            // already take care of keep truck data fresh. It's sent in the response only because the web needs it.
            ownerColumn = new ContentValues(1);
            ownerColumn.put(PublicContract.Truck.OWNER_ID, userAccount.getUserId());
            for (Truck truck : response.trucks) {
                WhereClause where = new WhereClause.Builder()
                        .where(PublicContract.Truck.ID, EQUALS, truck.id)
                        .build();
                database.update(Tables.TRUCK_PROPERTIES, ownerColumn, where.selection, where.selectionArgs);
            }

            database.setTransactionSuccessful();
            wasSuccessful = true;
        } finally {
            database.endTransaction();
        }

        if (wasSuccessful) {
            bus.post(new CompletedEvent());
        }
    }

    public static class CompletedEvent {
    }
}
