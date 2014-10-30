package com.truckmuncher.truckmuncher;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.truckmuncher.api.trucks.ActiveTrucksRequest;
import com.truckmuncher.api.trucks.ActiveTrucksResponse;
import com.truckmuncher.truckmuncher.data.ApiManager;
import com.truckmuncher.truckmuncher.data.Contract;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class ActiveTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";
    public static final String ARG_SEARCH_QUERY = "search_query";

    private double latitude;
    private double longitude;
    private String searchQuery;

    public ActiveTrucksService() {
        super(ActiveTrucksService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        latitude = intent.getDoubleExtra(ARG_LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(ARG_LONGITUDE, 0.0);
        searchQuery = intent.getStringExtra(ARG_SEARCH_QUERY);

        ActiveTrucksRequest request = new ActiveTrucksRequest(latitude, longitude, searchQuery);

        ApiManager.getTruckService(this).getActiveTrucks(request, new Callback<ActiveTrucksResponse>() {
            @Override
            public void success(ActiveTrucksResponse activeTrucksResponse, Response response) {
                List<ActiveTrucksResponse.Truck> trucks = activeTrucksResponse.trucks;
                ContentValues[] contentValues = new ContentValues[trucks.size()];
                for (int i = 0, max = trucks.size(); i < max; i++) {
                    ActiveTrucksResponse.Truck truck = trucks.get(i);
                    ContentValues values = new ContentValues();
                    values.put(Contract.TruckEntry.COLUMN_INTERNAL_ID, truck.id);
                    values.put(Contract.TruckEntry.COLUMN_LATITUDE, truck.latitude);
                    values.put(Contract.TruckEntry.COLUMN_LONGITUDE, truck.longitude);
                    values.put(Contract.TruckEntry.COLUMN_IS_SERVING, 1);
                    contentValues[i] = values;
                }

                getContentResolver().bulkInsert(Contract.TruckEntry.CONTENT_URI, contentValues);
            }

            @Override
            public void failure(RetrofitError error) {
                Intent errorIntent = new Intent();

                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    Timber.e("Experienced a network error: %s", error.getMessage());
                    errorIntent.putExtra(ARG_MESSAGE, getString(R.string.error_network));
                } else {
                    com.truckmuncher.api.exceptions.Error apiError = (com.truckmuncher.api.exceptions.Error) error.getBodyAs(com.truckmuncher.api.exceptions.Error.class.getComponentType());
                    Timber.e("Got an error while getting active trucks. Error code: %d", apiError.internalCode);
                    errorIntent.putExtra(ARG_MESSAGE, apiError.userMessage);
                }
                LocalBroadcastManager.getInstance(ActiveTrucksService.this).sendBroadcast(errorIntent);
            }
        });
    }
}
