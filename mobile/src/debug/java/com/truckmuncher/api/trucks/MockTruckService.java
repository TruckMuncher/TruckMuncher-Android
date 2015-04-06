package com.truckmuncher.api.trucks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;

public class MockTruckService implements TruckService {

    @Override
    public ActiveTrucksResponse getActiveTrucks(@Body ActiveTrucksRequest request) throws RetrofitError {
        List<ActiveTrucksResponse.Truck> trucks = new ArrayList<>();
        int numTrucks = 10;
        for (int i = 0; i < numTrucks; i++) {
            ActiveTrucksResponse.Truck truck = new ActiveTrucksResponse.Truck.Builder()
                    .id("Truck" + i)
                    .latitude(getRandomLatitude())
                    .longitude(getRandomLongitude())
                    .build();
            trucks.add(truck);
        }
        return new ActiveTrucksResponse(trucks);
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
                    .keywords(Arrays.asList("These", "Are", "Keywords"))
                    .primaryColor("#0000FF")
                    .secondaryColor("#FF0000")
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
        List<Truck> trucks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Truck truck = new Truck.Builder()
                    .id("Truck" + i)
                    .name("Truck" + i)
                    .keywords(Arrays.asList("These", "Are", "Keywords"))
                    .primaryColor("#0000FF")
                    .secondaryColor("#FF0000")
                    .build();
            trucks.add(truck);
        }
        return new TruckProfilesResponse(trucks);
    }

    @Override
    public void getTruckProfiles(@Body TruckProfilesRequest request, Callback<TruckProfilesResponse> callback) {
        callback.success(getTruckProfiles(request), null);
    }

    @Override
    public Truck modifyTruckProfile(@Body ModifyTruckRequest request) throws RetrofitError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void modifyTruckProfile(@Body ModifyTruckRequest request, Callback<Truck> callback) {
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

    @Override
    public ApprovalResponse requestApproval(@Body ApprovalRequest request) throws RetrofitError {
        return null;
    }

    @Override
    public void requestApproval(@Body ApprovalRequest request, Callback<ApprovalResponse> callback) {

    }

    @Override
    public ApprovalStatusResponse checkApprovalStatus(@Body ApprovalStatusRequest request) throws RetrofitError {
        return null;
    }

    @Override
    public void checkApprovalStatus(@Body ApprovalStatusRequest request, Callback<ApprovalStatusResponse> callback) {

    }

    /**
     * Generates a random latitude between 43.03 and 43.05 (somewhere in Milwaukee).
     *
     * @return A random latitude between 43.03 and 43.05 (somewhere in Milwaukee).
     */
    private double getRandomLatitude() {
        double base = 43.03;
        double offset = Math.random() * 0.02;

        return base + offset;
    }

    /**
     * Generates a random longitude between -87.90 and -87.92 (somewhere in Milwaukee).
     *
     * @return A random longitude between -87.90 and -87.92 (somewhere in Milwaukee).
     */
    private double getRandomLongitude() {
        double base = -87.92;
        double offset = Math.random() * 0.02;

        return base + offset;
    }
}
