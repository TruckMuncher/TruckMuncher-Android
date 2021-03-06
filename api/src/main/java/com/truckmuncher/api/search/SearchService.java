// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/search.proto
package com.truckmuncher.api.search;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * The SearchService is used to offer full text search functionality to find trucks given filter criteria.
 */
public interface SearchService {
  /**
   * A simple search to find all trucks containing all terms in the given request
   */
  @POST("/com.truckmuncher.api.search.SearchService/simpleSearch")
  SimpleSearchResponse simpleSearch(@Body SimpleSearchRequest request)
      throws RetrofitError;

  /**
   * A simple search to find all trucks containing all terms in the given request
   */
  @POST("/com.truckmuncher.api.search.SearchService/simpleSearch")
  void simpleSearch(@Body SimpleSearchRequest request, Callback<SimpleSearchResponse> callback);
}
