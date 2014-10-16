package com.truckmuncher.truckmuncher.data;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.truckmuncher.api.exceptions.Error;
import com.truckmuncher.api.trucks.ServingModeRequest;
import com.truckmuncher.api.trucks.ServingModeResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;

public class ServingModeService extends IntentService {

    public ServingModeService() {
        super(ServingModeService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getData() == null) {
            Timber.e("Launched without a data uri. No work will be done.");
            return;
        }
        Uri uri = intent.getData();

        Cursor cursor = getContentResolver().query(uri, TruckQuery.PROJECTION, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            Timber.e("The provided uri: %s, was not found in the database.", uri);
            return;
        }

        final ServingModeRequest request = new ServingModeRequest.Builder()
                .isInServingMode(cursor.getInt(TruckQuery.IS_SERVING) == 1)
                .truckId(cursor.getString(TruckQuery.INTERNAL_ID))
                .truckLatitude(cursor.getDouble(TruckQuery.LATITUDE))
                .truckLongitude(cursor.getDouble(TruckQuery.LONGITUDE))
                .build();
        ApiManager.getTruckService(this).modifyServingMode(request, new Callback<ServingModeResponse>() {
            @Override
            public void success(ServingModeResponse servingModeResponse, Response response) {
                // No-op
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    Timber.e("Experienced a network error: %s", error.getMessage());
                } else {
                    Error apiError = (Error) error.getBodyAs(com.truckmuncher.api.exceptions.Error.class.getComponentType());
                    Timber.e("Got an error while updating the serving mode for truck id=%s. Error code: %d", request.truckId, apiError.internalCode);
                    // TODO notify the user that the request failed
                }
            }
        });
        cursor.close();
    }

    interface TruckQuery {
        static final String[] PROJECTION = new String[]{TruckEntry.COLUMN_INTERNAL_ID, TruckEntry.COLUMN_IS_SERVING, TruckEntry.COLUMN_LATITUDE, TruckEntry.COLUMN_LONGITUDE};
        static final int INTERNAL_ID = 0;
        static final int IS_SERVING = 1;
        static final int LATITUDE = 2;
        static final int LONGITUDE = 3;
    }
}