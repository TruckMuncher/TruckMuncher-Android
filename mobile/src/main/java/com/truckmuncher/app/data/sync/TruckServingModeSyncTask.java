package com.truckmuncher.app.data.sync;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.truckmuncher.api.trucks.ServingModeRequest;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.app.data.ApiException;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;

import static com.truckmuncher.app.data.Contract.suppressNotify;
import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public final class TruckServingModeSyncTask extends SyncTask {

    private final ContentProviderClient provider;
    private final TruckService truckService;
    private final ApiExceptionResolver apiExceptionResolver;

    public TruckServingModeSyncTask(ContentProviderClient provider, TruckService truckService, ApiExceptionResolver apiExceptionResolver) {
        this.provider = provider;
        this.truckService = truckService;
        this.apiExceptionResolver = apiExceptionResolver;
    }

    @Override
    protected ApiResult sync(SyncResult syncResult) throws RemoteException {
        WhereClause whereClause = new WhereClause.Builder()
                .where(Contract.TruckState.IS_DIRTY, EQUALS, true)
                .build();
        Cursor cursor = provider.query(PublicContract.TRUCK_URI, TruckServingModeQuery.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
        if (!cursor.moveToFirst()) {

            // Cursor is empty. Probably already synced this.
            cursor.close();
            return ApiResult.OK;
        }

        ApiResult retVal = ApiResult.OK;

        do {
            ServingModeRequest request = new ServingModeRequest.Builder()
                    .isInServingMode(cursor.getInt(TruckServingModeQuery.IS_SERVING) == 1)
                    .truckId(cursor.getString(TruckServingModeQuery.ID))
                    .truckLatitude(cursor.getDouble(TruckServingModeQuery.LATITUDE))
                    .truckLongitude(cursor.getDouble(TruckServingModeQuery.LONGITUDE))
                    .build();

            try {
                truckService.modifyServingMode(request);

                // Clear the dirty state
                ContentValues values = new ContentValues();
                values.put(Contract.TruckState.IS_DIRTY, false);

                // Since we're clearing an internal state, don't notify listeners
                Uri uri = suppressNotify(Contract.TRUCK_STATE_URI);
                WhereClause q = new WhereClause.Builder()
                        .where(PublicContract.Truck.ID, EQUALS, request.truckId)
                        .build();
                provider.update(uri, values, q.selection, q.selectionArgs);
            } catch (ApiException e) {
                ApiResult result = apiExceptionResolver.resolve(e);

                // Need to use the most recoverable result
                if (result == ApiResult.SHOULD_RETRY || retVal == ApiResult.SHOULD_RETRY) {
                    retVal = ApiResult.SHOULD_RETRY;
                } else if (result == ApiResult.TEMPORARY_ERROR || retVal == ApiResult.TEMPORARY_ERROR) {
                    retVal = ApiResult.TEMPORARY_ERROR;
                } else if (result == ApiResult.NEEDS_USER_INPUT || retVal == ApiResult.NEEDS_USER_INPUT) {
                    retVal = ApiResult.NEEDS_USER_INPUT;
                } else if (result == ApiResult.PERMANENT_ERROR || retVal == ApiResult.PERMANENT_ERROR) {
                    retVal = ApiResult.PERMANENT_ERROR;
                } else {
                    retVal = result;
                }
            }
        } while (cursor.moveToNext());
        cursor.close();
        return retVal;
    }

    interface TruckServingModeQuery {
        static final String[] PROJECTION = new String[]{
                PublicContract.Truck.ID,
                PublicContract.Truck.IS_SERVING,
                PublicContract.Truck.LATITUDE,
                PublicContract.Truck.LONGITUDE
        };
        static final int ID = 0;
        static final int IS_SERVING = 1;
        static final int LATITUDE = 2;
        static final int LONGITUDE = 3;
    }
}
