package com.truckmuncher.app.vendor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.truckmuncher.app.App;

import javax.inject.Inject;

public class VendorTrucksService extends IntentService {

    @Inject
    VendorTruckStateResolver vendorTruckStateResolver;

    public VendorTrucksService() {
        super(VendorTrucksService.class.getSimpleName());
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, VendorTrucksService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        vendorTruckStateResolver.resolveState();
    }
}
