package com.truckmuncher.truckmuncher.vendor;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.ApiManager;
import com.truckmuncher.truckmuncher.data.Contract;

import java.util.List;

import timber.log.Timber;

public class VendorTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";

    public VendorTrucksService() {
        super(VendorTrucksService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            TrucksForVendorResponse response = ApiManager.getTruckService(this).getTrucksForVendor(new TrucksForVendorRequest());

            List<Truck> trucks = response.trucks;
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
        } catch (ApiException e) {
            Timber.e("Got an error while getting trucks for vendor.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(VendorTrucksService.this).sendBroadcast(errorIntent);
        }
    }
}
