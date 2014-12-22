package com.truckmuncher.truckmuncher;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.trucks.ActiveTrucksRequest;
import com.truckmuncher.api.trucks.ActiveTrucksResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.PublicContract;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

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

    @Override
    protected void onHandleIntent(Intent intent) {
        App.inject(this, this);

        double latitude = intent.getDoubleExtra(ARG_LATITUDE, Double.MAX_VALUE);
        double longitude = intent.getDoubleExtra(ARG_LONGITUDE, Double.MAX_VALUE);
        String searchQuery = intent.getStringExtra(ARG_SEARCH_QUERY);

        if (latitude == Double.MAX_VALUE || longitude == Double.MAX_VALUE) {
            throw new IllegalArgumentException("Latitude and/or longitude not provided");
        }

        ActiveTrucksRequest request = new ActiveTrucksRequest(latitude, longitude, searchQuery);

        try {
            ActiveTrucksResponse response = truckService.getActiveTrucks(request);

            List<ActiveTrucksResponse.Truck> trucks = response.trucks;
            ContentValues[] contentValues = new ContentValues[trucks.size()];
            for (int i = 0, max = trucks.size(); i < max; i++) {
                ActiveTrucksResponse.Truck truck = trucks.get(i);
                ContentValues values = new ContentValues();
                values.put(PublicContract.TruckState.ID, truck.id);
                values.put(PublicContract.TruckState.LATITUDE, truck.latitude);
                values.put(PublicContract.TruckState.LONGITUDE, truck.longitude);
                values.put(PublicContract.TruckState.IS_SERVING, true);
                contentValues[i] = values;
            }

            getContentResolver().delete(PublicContract.TRUCK_STATE_URI, null, null);
            getContentResolver().bulkInsert(PublicContract.TRUCK_STATE_URI, contentValues);
        } catch (ApiException e) {
            Timber.e("Got an error while getting active trucks.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(ActiveTrucksService.this).sendBroadcast(errorIntent);
        }
    }
}
