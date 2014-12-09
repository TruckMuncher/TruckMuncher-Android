package com.truckmuncher.truckmuncher.dagger;

import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.menu.MockMenuService;
import com.truckmuncher.api.trucks.MockTruckService;
import com.truckmuncher.api.trucks.TruckService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.MockRestAdapter;
import retrofit.RestAdapter;

@Module(overrides = true, library = true, complete = false)
public class MockNetworkModule {

    @Singleton
    @Provides
    public RestAdapter provideRestAdapter(RestAdapter.Builder builder) {
        return builder
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    @Singleton
    @Provides
    public TruckService provideTruckService(RestAdapter adapter) {
        return MockRestAdapter.from(adapter).create(TruckService.class, new MockTruckService());
    }

    @Singleton
    @Provides
    public MenuService provideMenuService(RestAdapter adapter) {
        return MockRestAdapter.from(adapter).create(MenuService.class, new MockMenuService());
    }
}