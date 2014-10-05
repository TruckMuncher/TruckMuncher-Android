// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/healthcheck.proto
package com.truckmuncher.api.healthcheck;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Check the health of the server and your connection
 */
public interface HealthCheckService {
    /**
     * Runs a light test across all services, and checks your request for correctness.
     */
    @POST("/com.truckmuncher.api.healthcheck.HealthCheckService/healthcheck")
    HealthResponse healthcheck(@Body HealthRequest request)
            throws RetrofitError;

    /**
     * Runs a light test across all services, and checks your request for correctness.
     */
    @POST("/com.truckmuncher.api.healthcheck.HealthCheckService/healthcheck")
    void healthcheck(@Body HealthRequest request, Callback<HealthResponse> callback);
}
