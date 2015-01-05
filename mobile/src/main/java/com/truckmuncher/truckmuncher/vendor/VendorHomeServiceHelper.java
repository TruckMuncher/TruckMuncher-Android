package com.truckmuncher.truckmuncher.vendor;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;
import com.truckmuncher.truckmuncher.data.SimpleAsyncQueryHandler;
import com.truckmuncher.truckmuncher.data.sql.WhereClause;

import static com.truckmuncher.truckmuncher.data.sql.WhereClause.Operator.EQUALS;

public class VendorHomeServiceHelper {

    void changeServingState(Context context, String truckId, boolean isServing, Location location) {
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.LATITUDE, location.getLatitude());
        values.put(PublicContract.Truck.LONGITUDE, location.getLongitude());
        values.put(PublicContract.Truck.IS_SERVING, isServing);
        values.put(Contract.TruckState.IS_DIRTY, true);

        Uri uri = Contract.syncToNetwork(Contract.TRUCK_STATE_URI);
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.ID, EQUALS, truckId)
                .build();

        AsyncQueryHandler queryHandler = new SimpleAsyncQueryHandler(context.getContentResolver());
        queryHandler.startUpdate(0, null, uri, values, whereClause.selection, whereClause.selectionArgs);
    }
}
