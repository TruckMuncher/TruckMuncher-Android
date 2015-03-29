package com.truckmuncher.app.dagger;

import com.truckmuncher.app.customer.ActiveTrucksService;
import com.truckmuncher.app.customer.GetTruckProfilesService;
import com.truckmuncher.app.customer.SimpleSearchService;
import com.truckmuncher.app.data.sync.SyncAdapter;
import com.truckmuncher.app.gcm.GcmRegistrationService;
import com.truckmuncher.app.menu.MenuUpdateService;
import com.truckmuncher.app.vendor.VendorTrucksService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = NetworkModule.class, dependencies = UserComponent.class)
public interface NetworkComponent {
    void inject(ActiveTrucksService injectable);

    void inject(VendorTrucksService injectable);

    void inject(SyncAdapter injectable);

    void inject(MenuUpdateService injectable);

    void inject(GetTruckProfilesService injectable);

    void inject(SimpleSearchService injectable);

    void inject(GcmRegistrationService injectable);
}
