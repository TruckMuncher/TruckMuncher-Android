package com.truckmuncher.truckmuncher.data;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.menu.MockMenuService;
import com.truckmuncher.api.trucks.MockTruckService;
import com.truckmuncher.api.trucks.TruckService;

import retrofit.MockRestAdapter;
import retrofit.RestAdapter;

public class ApiManagerImpl extends ApiManager {

    private MockRestAdapter adapter;
    private MenuService menuService;
    private TruckService truckService;

    ApiManagerImpl(Context context) {
        super(context);
    }

    @Override
    protected RestAdapter.Builder configureRestAdapter(Context context, OkHttpClient client) {
        return super.configureRestAdapter(context, client)
                .setLogLevel(RestAdapter.LogLevel.FULL);
    }

    @Override
    protected MenuService getMenuService() {
        if (menuService == null) {
            if (adapter == null) {
                adapter = MockRestAdapter.from(getAdapter());
            }
            menuService = adapter.create(MenuService.class, new MockMenuService());
        }
        return menuService;
    }

    @Override
    protected TruckService getTruckService() {
        if (truckService == null) {
            if (adapter == null) {
                adapter = MockRestAdapter.from(getAdapter());
            }
            truckService = adapter.create(TruckService.class, new MockTruckService());
        }
        return truckService;
    }
}
