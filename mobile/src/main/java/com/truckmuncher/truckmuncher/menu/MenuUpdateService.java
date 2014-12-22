package com.truckmuncher.truckmuncher.menu;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.truckmuncher.api.menu.Category;
import com.truckmuncher.api.menu.FullMenusRequest;
import com.truckmuncher.api.menu.FullMenusResponse;
import com.truckmuncher.api.menu.Menu;
import com.truckmuncher.api.menu.MenuItem;
import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.truckmuncher.App;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class MenuUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Inject
    MenuService menuService;

    private GoogleApiClient apiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        App.inject(this, this);
        apiClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apiClient.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (location != null) {
            doUpdate(location);
        } else {
            LocationRequest request = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_LOW_POWER)        // City level
                    .setInterval(5000);     // 5 seconds. We want it quickly b/c we'll stop w/ location right away, but might need ot update UI
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO Handle
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
            doUpdate(location);
        }
    }

    private void doUpdate(@NonNull Location location) {
        FullMenusRequest request = new FullMenusRequest.Builder()
                .includeAvailability(true)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        menuService.getFullMenus(request, new Callback<FullMenusResponse>() {
            @Override
            public void success(final FullMenusResponse fullMenusResponse, Response response) {

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        List<Menu> menus = fullMenusResponse.menus;

                        List<ContentValues> categoryContentValues = new ArrayList<>();
                        List<ContentValues> menuItemContentValues = new ArrayList<>();
                        for (Menu menu : menus) {

                            List<Category> categories = menu.categories;
                            for (Category category : categories) {
                                ContentValues categoryValues = new ContentValues();
                                categoryValues.put(Contract.Category.ID, category.id);
                                categoryValues.put(Contract.Category.NAME, category.name);
                                categoryValues.put(Contract.Category.NOTES, category.notes);
                                categoryValues.put(Contract.Category.ORDER_IN_MENU, category.orderInMenu);
                                categoryValues.put(Contract.Category.TRUCK_ID, menu.truckId);
                                categoryContentValues.add(categoryValues);


                                List<MenuItem> menuItems = category.menuItems;
                                for (MenuItem item : menuItems) {
                                    ContentValues itemValues = new ContentValues();
                                    itemValues.put(Contract.MenuItem.ID, item.id);
                                    itemValues.put(Contract.MenuItem.IS_AVAILABLE, item.isAvailable);
                                    itemValues.put(Contract.MenuItem.PRICE, item.price);
                                    itemValues.put(Contract.MenuItem.ORDER_IN_CATEGORY, item.orderInCategory);
                                    itemValues.put(Contract.MenuItem.NOTES, item.notes);
                                    itemValues.put(Contract.MenuItem.NAME, item.name);
                                    itemValues.put(Contract.MenuItem.TAGS, Contract.convertListToString(item.tags));
                                    itemValues.put(Contract.MenuItem.CATEGORY_ID, category.id);
                                    menuItemContentValues.add(itemValues);
                                }
                            }
                        }

                        ContentValues[] categoryInsert = categoryContentValues.toArray(new ContentValues[categoryContentValues.size()]);
                        ContentValues[] menuItemInsert = menuItemContentValues.toArray(new ContentValues[menuItemContentValues.size()]);
                        getContentResolver().bulkInsert(PublicContract.CATEGORY_URI, categoryInsert);
                        getContentResolver().bulkInsert(PublicContract.MENU_ITEM_URI, menuItemInsert);


                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {

                        // Need to notify on the Menu View URI b/c ContentProvider won't
                        getContentResolver().notifyChange(Contract.MenuEntry.CONTENT_URI, null);
                        stopSelf();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void failure(RetrofitError error) {

                // All logging has already been performed at this point.
                Timber.e("Got an error while getting full menus.");
                stopSelf();
            }
        });
    }
}
