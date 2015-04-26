package com.truckmuncher.app.customer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.truckmuncher.api.trucks.ActiveTrucksRequest;
import com.truckmuncher.api.trucks.ActiveTrucksResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.app.App;
import com.truckmuncher.app.data.ApiException;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.TruckMuncherContentProvider;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ActiveTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_SEARCH_QUERY = "search_query";

    @Inject
    TruckService truckService;

    public ActiveTrucksService() {
        super(ActiveTrucksService.class.getSimpleName());
    }

    public static Intent newIntent(Context context, LatLng location) {
        Intent intent = new Intent(context, ActiveTrucksService.class);
        intent.putExtra(ARG_LOCATION, location);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        LatLng location = intent.getParcelableExtra(ARG_LOCATION);
        String searchQuery = intent.getStringExtra(ARG_SEARCH_QUERY);

        ActiveTrucksRequest request = new ActiveTrucksRequest.Builder()
                .latitude(location.latitude)
                .longitude(location.longitude)
                .searchQuery(searchQuery)
                .build();

        try {
            ActiveTrucksResponse response = truckService.getActiveTrucks(request);

            if (response == null) {
                return;
            }

            List<ActiveTrucksResponse.Truck> trucks = response.trucks;
            ContentValues[] contentValues = new ContentValues[trucks.size()];
            String[] truckIds = new String[trucks.size()];

            for (int i = 0, max = trucks.size(); i < max; i++) {
                ActiveTrucksResponse.Truck truck = trucks.get(i);
                ContentValues values = new ContentValues();
                values.put(PublicContract.Truck.ID, truck.id);
                values.put(PublicContract.Truck.LATITUDE, truck.latitude);
                values.put(PublicContract.Truck.LONGITUDE, truck.longitude);
                values.put(PublicContract.Truck.IS_SERVING, true);
                values.put(PublicContract.Truck.MATCHED_SEARCH, true);
                contentValues[i] = values;

                truckIds[i] = truck.id;
            }

            getContentResolver().bulkInsert(Contract.TRUCK_STATE_URI, contentValues);

            Bundle bundle = new Bundle();
            bundle.putStringArray(TruckMuncherContentProvider.ARG_ID_ARRAY, truckIds);

            getContentResolver().call(Contract.TRUCK_STATE_URI,
                    TruckMuncherContentProvider.METHOD_UPDATE_INACTIVE_TRUCKS, null, bundle);
        } catch (ApiException e) {
            Timber.e(e, "Got an error while getting active trucks.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(ActiveTrucksService.this).sendBroadcast(errorIntent);
        }
    }
}
