package com.truckmuncher.truckmuncher.customer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.trucks.ActiveTrucksRequest;
import com.truckmuncher.api.trucks.ActiveTrucksResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.truckmuncher.App;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.guava.common.base.Preconditions.checkArgument;

public class ActiveTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_SEARCH_QUERY = "search_query";

    @Inject
    TruckService truckService;

    public ActiveTrucksService() {
        super(ActiveTrucksService.class.getSimpleName());
    }

    public static Intent newIntent(Context context, double latitude, double longitude) {
        Intent intent = new Intent(context, ActiveTrucksService.class);
        intent.putExtra(ARG_LATITUDE, latitude);
        intent.putExtra(ARG_LONGITUDE, longitude);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.inject(this, this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        double latitude = intent.getDoubleExtra(ARG_LATITUDE, Double.MAX_VALUE);
        double longitude = intent.getDoubleExtra(ARG_LONGITUDE, Double.MAX_VALUE);
        String searchQuery = intent.getStringExtra(ARG_SEARCH_QUERY);

        checkArgument(latitude != Double.MAX_VALUE, "Latitude not provided");
        checkArgument(longitude != Double.MAX_VALUE, "Longitude not provided");

        ActiveTrucksRequest request = new ActiveTrucksRequest(latitude, longitude, searchQuery);

        try {
            ActiveTrucksResponse response = truckService.getActiveTrucks(request);

            List<ActiveTrucksResponse.Truck> trucks = response.trucks;
            ContentValues[] contentValues = new ContentValues[trucks.size()];
            for (int i = 0, max = trucks.size(); i < max; i++) {
                ActiveTrucksResponse.Truck truck = trucks.get(i);
                ContentValues values = new ContentValues();
                values.put(PublicContract.Truck.ID, truck.id);
                values.put(PublicContract.Truck.LATITUDE, truck.latitude);
                values.put(PublicContract.Truck.LONGITUDE, truck.longitude);
                values.put(PublicContract.Truck.IS_SERVING, true);
                contentValues[i] = values;
            }

            getContentResolver().delete(Contract.TRUCK_STATE_URI, null, null);
            getContentResolver().bulkInsert(Contract.TRUCK_STATE_URI, contentValues);
        } catch (ApiException e) {
            Timber.e(e, "Got an error while getting active trucks.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(ActiveTrucksService.this).sendBroadcast(errorIntent);
        }
    }
}
