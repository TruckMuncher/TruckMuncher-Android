// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * The MenuService is used to interact with menus. The service is built to separate persistent state from volatile state when possible.
 */
public interface MenuService {
  /**
   * Returns the full MenuItems availability for the entire region.
   */
  @POST("/com.truckmuncher.api.menu.MenuService/getMenuItemAvailability")
  MenuItemAvailabilityResponse getMenuItemAvailability(@Body MenuItemAvailabilityRequest request)
      throws RetrofitError;

  /**
   * Returns the full MenuItems availability for the entire region.
   */
  @POST("/com.truckmuncher.api.menu.MenuService/getMenuItemAvailability")
  void getMenuItemAvailability(@Body MenuItemAvailabilityRequest request, Callback<MenuItemAvailabilityResponse> callback);
  /**
   * Get all of the menus in the user's region.
   */
  @POST("/com.truckmuncher.api.menu.MenuService/getFullMenus")
  FullMenusResponse getFullMenus(@Body FullMenusRequest request)
      throws RetrofitError;

  /**
   * Get all of the menus in the user's region.
   */
  @POST("/com.truckmuncher.api.menu.MenuService/getFullMenus")
  void getFullMenus(@Body FullMenusRequest request, Callback<FullMenusResponse> callback);
  /**
   * Use this to get a single menu.
   */
  @POST("/com.truckmuncher.api.menu.MenuService/getMenu")
  MenuResponse getMenu(@Body MenuRequest request)
      throws RetrofitError;

  /**
   * Use this to get a single menu.
   */
  @POST("/com.truckmuncher.api.menu.MenuService/getMenu")
  void getMenu(@Body MenuRequest request, Callback<MenuResponse> callback);
  /**
   * Declare which MenuItems are changing in availability.
   *
   * This call requires Vendor authorization
   */
  @POST("/com.truckmuncher.api.menu.MenuService/modifyMenuItemAvailability")
  ModifyMenuItemAvailabilityResponse modifyMenuItemAvailability(@Body ModifyMenuItemAvailabilityRequest request)
      throws RetrofitError;

  /**
   * Declare which MenuItems are changing in availability.
   *
   * This call requires Vendor authorization
   */
  @POST("/com.truckmuncher.api.menu.MenuService/modifyMenuItemAvailability")
  void modifyMenuItemAvailability(@Body ModifyMenuItemAvailabilityRequest request, Callback<ModifyMenuItemAvailabilityResponse> callback);
}
