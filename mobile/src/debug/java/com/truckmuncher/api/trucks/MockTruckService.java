package com.truckmuncher.api.trucks;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;

public class MockTruckService implements TruckService {

    @Override
    public ActiveTrucksResponse getActiveTrucks(@Body ActiveTrucksRequest request) throws RetrofitError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void getActiveTrucks(@Body ActiveTrucksRequest request, Callback<ActiveTrucksResponse> callback) {
        callback.success(getActiveTrucks(request), null);
    }

    @Override
    public TrucksForVendorResponse getTrucksForVendor(@Body TrucksForVendorRequest request) throws RetrofitError {
        List<Truck> trucks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Truck truck = new Truck.Builder()
                    .id("Truck" + i)
                    .imageUrl("http://api.truckmuncher.com/images/truck/" + i)
                    .name("Truck" + i)
                    .build();
            trucks.add(truck);
        }
        return new TrucksForVendorResponse(trucks, false);
    }

    @Override
    public void getTrucksForVendor(@Body TrucksForVendorRequest request, Callback<TrucksForVendorResponse> callback) {
        callback.success(getTrucksForVendor(request), null);
    }

    @Override
    public TruckProfilesResponse getTruckProfiles(@Body TruckProfilesRequest request) throws RetrofitError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void getTruckProfiles(@Body TruckProfilesRequest request, Callback<TruckProfilesResponse> callback) {
        callback.success(getTruckProfiles(request), null);
    }

    @Override
    public Truck modifyTruckProfile(@Body Truck request) throws RetrofitError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void modifyTruckProfile(@Body Truck request, Callback<Truck> callback) {
        callback.success(modifyTruckProfile(request), null);
    }

    @Override
    public ServingModeResponse modifyServingMode(@Body ServingModeRequest request) throws RetrofitError {
        return new ServingModeResponse();
    }

    @Override
    public void modifyServingMode(@Body ServingModeRequest request, Callback<ServingModeResponse> callback) {
        callback.success(modifyServingMode(request), null);
    }
}
