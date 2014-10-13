// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/trucks.proto
package com.truckmuncher.api.trucks;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * TODO
 */
public interface TruckService {
    /**
     * Get the locations of active food trucks in the Region in which the search is performed.
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/getActiveTrucks")
    ActiveTrucksResponse getActiveTrucks(@Body ActiveTrucksRequest request)
            throws RetrofitError;

    /**
     * Get the locations of active food trucks in the Region in which the search is performed.
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/getActiveTrucks")
    void getActiveTrucks(@Body ActiveTrucksRequest request, Callback<ActiveTrucksResponse> callback);

    /**
     * If a truck for the vendor does not exist, a new truck will be created.
     * When using from a native app, check the response and handle the case of an incomplete truck.
     * <p/>
     * This call requires Vendor authorization
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/getTrucksForVendor")
    TrucksForVendorResponse getTrucksForVendor(@Body TrucksForVendorRequest request)
            throws RetrofitError;

    /**
     * If a truck for the vendor does not exist, a new truck will be created.
     * When using from a native app, check the response and handle the case of an incomplete truck.
     * <p/>
     * This call requires Vendor authorization
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/getTrucksForVendor")
    void getTrucksForVendor(@Body TrucksForVendorRequest request, Callback<TrucksForVendorResponse> callback);

    /**
     * Get the profiles of all the food trucks in the user's Region.
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/getTruckProfiles")
    TruckProfilesResponse getTruckProfiles(@Body TruckProfilesRequest request)
            throws RetrofitError;

    /**
     * Get the profiles of all the food trucks in the user's Region.
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/getTruckProfiles")
    void getTruckProfiles(@Body TruckProfilesRequest request, Callback<TruckProfilesResponse> callback);

    /**
     * Use this to create or update a truck. Any data sent in the request will be used, even if the truck did not previously exist.
     * <p/>
     * This call requires Vendor authorization
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/modifyTruckProfile")
    Truck modifyTruckProfile(@Body Truck request)
            throws RetrofitError;

    /**
     * Use this to create or update a truck. Any data sent in the request will be used, even if the truck did not previously exist.
     * <p/>
     * This call requires Vendor authorization
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/modifyTruckProfile")
    void modifyTruckProfile(@Body Truck request, Callback<Truck> callback);

    /**
     * Modify the serving mode for a truck.
     * <p/>
     * This call requires Vendor authorization
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/modifyServingMode")
    ServingModeResponse modifyServingMode(@Body ServingModeRequest request)
            throws RetrofitError;

    /**
     * Modify the serving mode for a truck.
     * <p/>
     * This call requires Vendor authorization
     */
    @POST("/com.truckmuncher.api.trucks.TruckService/modifyServingMode")
    void modifyServingMode(@Body ServingModeRequest request, Callback<ServingModeResponse> callback);
}