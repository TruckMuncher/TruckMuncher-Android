package com.truckmuncher.app.customer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.wire.Wire;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckProfilesRequest;
import com.truckmuncher.api.trucks.TruckProfilesResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.app.App;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sql.WhereClause;

import java.util.List;

import javax.inject.Inject;

import static com.truckmuncher.app.data.PublicContract.convertListToString;
import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class GetTruckProfilesService extends IntentService {

    private static final String ARG_LATITUDE = "arg_latitude";
    private static final String ARG_LONGITUDE = "arg_longitude";

    @Inject
    TruckService truckService;
    @Inject
    SQLiteOpenHelper openHelper;

    public GetTruckProfilesService() {
        super(GetTruckProfilesService.class.getName());
    }

    public static Intent newIntent(Context context, double latitude, double longitude) {
        Intent intent = new Intent(context, GetTruckProfilesService.class);
        intent.putExtra(ARG_LATITUDE, latitude);
        intent.putExtra(ARG_LONGITUDE, longitude);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TruckProfilesRequest request = new TruckProfilesRequest.Builder()
                .latitude(intent.getDoubleExtra(ARG_LATITUDE, 0.0))
                .longitude(intent.getDoubleExtra(ARG_LONGITUDE, 0.0))
                .build();
        TruckProfilesResponse truckProfilesResponse = truckService.getTruckProfiles(request);

        List<Truck> trucks = truckProfilesResponse.trucks;

        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(Contract.TruckProperties.IS_DIRTY, 1);

            // Mark all trucks in the database as dirty so we can remove "dead" trucks
            db.update(Tables.TRUCK_PROPERTIES, values, null, null);

            // Add new trucks. Set dirty to false
            for (Truck truck : trucks) {
                if (Wire.get(truck.approved, Truck.DEFAULT_APPROVED)) {
                    values.put(PublicContract.Truck.ID, truck.id);
                    values.put(PublicContract.Truck.NAME, truck.name);
                    values.put(PublicContract.Truck.IMAGE_URL, truck.imageUrl);
                    values.put(PublicContract.Truck.KEYWORDS, convertListToString(truck.keywords));
                    values.put(PublicContract.Truck.COLOR_PRIMARY, truck.primaryColor);
                    values.put(PublicContract.Truck.COLOR_SECONDARY, truck.secondaryColor);
                    values.put(PublicContract.Truck.DESCRIPTION, truck.description);
                    values.put(PublicContract.Truck.PHONE_NUMBER, truck.phoneNumber);
                    values.put(PublicContract.Truck.WEBSITE, truck.website);
                    values.put(Contract.TruckProperties.IS_DIRTY, 0);

                    db.replace(Tables.TRUCK_PROPERTIES, null, values);
                }
            }

            // Remove remaining dirty trucks
            WhereClause where = new WhereClause.Builder()
                    .where(Contract.TruckProperties.IS_DIRTY, EQUALS, true)
                    .build();
            db.delete(Tables.TRUCK_PROPERTIES, where.selection, where.selectionArgs);

            db.setTransactionSuccessful();
            getContentResolver().notifyChange(PublicContract.TRUCK_URI, null);
        } finally {
            db.endTransaction();
        }
    }
}
