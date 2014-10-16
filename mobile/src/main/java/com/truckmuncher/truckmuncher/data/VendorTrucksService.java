package com.truckmuncher.truckmuncher.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.exceptions.Error;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.truckmuncher.R;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class VendorTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";

    public VendorTrucksService() {
        super(VendorTrucksService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ApiManager.getTruckService(this).getTrucksForVendor(new TrucksForVendorRequest(), new Callback<TrucksForVendorResponse>() {

            @Override
            public void success(TrucksForVendorResponse trucksForVendorResponse, Response response) {
                List<Truck> trucks = trucksForVendorResponse.trucks;
                ContentValues[] contentValues = new ContentValues[trucks.size()];
                for (int i = 0, max = trucks.size(); i < max; i++) {
                    Truck truck = trucks.get(i);
                    ContentValues values = new ContentValues();
                    values.put(Contract.TruckEntry.COLUMN_INTERNAL_ID, truck.id);
                    values.put(Contract.TruckEntry.COLUMN_NAME, truck.name);
                    values.put(Contract.TruckEntry.COLUMN_IMAGE_URL, truck.imageUrl);
                    values.put(Contract.TruckEntry.COLUMN_KEYWORDS, Contract.convertListToString(truck.keywords));
                    values.put(Contract.TruckEntry.COLUMN_OWNED_BY_CURRENT_USER, true);
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
                    com.truckmuncher.api.exceptions.Error apiError = (Error) error.getBodyAs(com.truckmuncher.api.exceptions.Error.class.getComponentType());
                    Timber.e("Got an error while getting trucks for vendor. Error code: %d", apiError.internalCode);
                    errorIntent.putExtra(ARG_MESSAGE, apiError.userMessage);
                }
                LocalBroadcastManager.getInstance(VendorTrucksService.this).sendBroadcast(errorIntent);
            }
        });
    }
}
