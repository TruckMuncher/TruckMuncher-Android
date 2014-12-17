package com.truckmuncher.truckmuncher.customer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckProfilesRequest;
import com.truckmuncher.api.trucks.TruckProfilesResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.truckmuncher.App;

import java.util.List;

import javax.inject.Inject;

import static com.truckmuncher.truckmuncher.data.Contract.TruckConstantEntry;
import static com.truckmuncher.truckmuncher.data.Contract.convertListToString;

public class GetTruckProfilesService extends IntentService {

    @Inject
    TruckService truckService;

    public GetTruckProfilesService() {
        super(GetTruckProfilesService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.inject(this, this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TruckProfilesResponse truckProfilesResponse = truckService.getTruckProfiles(new TruckProfilesRequest(null, null));

        List<Truck> trucks = truckProfilesResponse.trucks;
        ContentValues[] contentValues = new ContentValues[trucks.size()];
        for (int i = 0, max = trucks.size(); i < max; i++) {
            Truck truck = trucks.get(i);
            ContentValues values = new ContentValues();
            values.put(TruckConstantEntry.COLUMN_INTERNAL_ID, truck.id);
            values.put(TruckConstantEntry.COLUMN_NAME, truck.name);
            values.put(TruckConstantEntry.COLUMN_IMAGE_URL, truck.imageUrl);
            values.put(TruckConstantEntry.COLUMN_KEYWORDS, convertListToString(truck.keywords));
            values.put(TruckConstantEntry.COLUMN_COLOR_PRIMARY, truck.primaryColor);
            values.put(TruckConstantEntry.COLUMN_COLOR_SECONDARY, truck.secondaryColor);
            contentValues[i] = values;
        }

        getContentResolver().bulkInsert(TruckConstantEntry.CONTENT_URI, contentValues);
    }
}
