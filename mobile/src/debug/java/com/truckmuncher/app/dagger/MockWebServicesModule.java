package com.truckmuncher.app.dagger;

import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.api.auth.MockAuthService;
import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.menu.MockMenuService;
import com.truckmuncher.api.search.MockSearchService;
import com.truckmuncher.api.search.SearchService;
import com.truckmuncher.api.trucks.MockTruckService;
import com.truckmuncher.api.trucks.TruckService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.MockRestAdapter;
import retrofit.RestAdapter;

@Module(complete = false, library = true)
public class MockWebServicesModule {

    @Singleton
    @Provides
    public TruckService provideTruckService(RestAdapter adapter) {
        MockRestAdapter mockAdapter = MockRestAdapter.from(adapter);
        mockAdapter.setErrorPercentage(0);
        mockAdapter.setDelay(0);
        return mockAdapter.create(TruckService.class, new MockTruckService());
    }

    @Singleton
    @Provides
    public MenuService provideMenuService(RestAdapter adapter) {
        MockRestAdapter mockAdapter = MockRestAdapter.from(adapter);
        mockAdapter.setErrorPercentage(0);
        mockAdapter.setDelay(0);
        return mockAdapter.create(MenuService.class, new MockMenuService());
    }

    @Singleton
    @Provides
    public AuthService provideAuthService(RestAdapter adapter) {
        return MockRestAdapter.from(adapter).create(AuthService.class, new MockAuthService());
    }

    @Singleton
    @Provides
    public SearchService provideSearchService(RestAdapter adapter) {
        MockRestAdapter mockAdapter = MockRestAdapter.from(adapter);
        mockAdapter.setErrorPercentage(0);
        mockAdapter.setDelay(0);
        return mockAdapter.create(SearchService.class, new MockSearchService());
    }
}
