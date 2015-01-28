package com.truckmuncher.app.customer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckProfilesRequest;
import com.truckmuncher.api.trucks.TruckProfilesResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.app.App;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.truckmuncher.app.data.Contract.convertListToString;

public class GetTruckProfilesService extends IntentService {

    private static final String ARG_LATITUDE = "arg_latitude";
    private static final String ARG_LONGITUDE = "arg_longitude";

    @Inject
    TruckService truckService;

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
        App.inject(this, this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TruckProfilesRequest request = new TruckProfilesRequest.Builder()
                .latitude(intent.getDoubleExtra(ARG_LATITUDE, 0.0))
                .longitude(intent.getDoubleExtra(ARG_LONGITUDE, 0.0))
                .build();
        TruckProfilesResponse truckProfilesResponse = truckService.getTruckProfiles(request);
        Timber.d("Response: %s", truckProfilesResponse.toString());

        List<Truck> trucks = truckProfilesResponse.trucks;
        ContentValues[] contentValues = new ContentValues[trucks.size()];
        for (int i = 0, max = trucks.size(); i < max; i++) {
            Truck truck = trucks.get(i);
            ContentValues values = new ContentValues();
            values.put(PublicContract.Truck.ID, truck.id);
            values.put(PublicContract.Truck.NAME, truck.name);
            values.put(PublicContract.Truck.IMAGE_URL, truck.imageUrl);
            values.put(PublicContract.Truck.KEYWORDS, convertListToString(truck.keywords));
            values.put(PublicContract.Truck.COLOR_PRIMARY, truck.primaryColor);
            values.put(PublicContract.Truck.COLOR_SECONDARY, truck.secondaryColor);
            contentValues[i] = values;
        }

        getContentResolver().bulkInsert(Contract.TRUCK_PROPERTIES_URI, contentValues);
    }
}
