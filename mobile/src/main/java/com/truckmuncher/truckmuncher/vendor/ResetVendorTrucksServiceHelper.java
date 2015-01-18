package com.truckmuncher.truckmuncher.vendor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.truckmuncher.api.trucks.Truck;

import java.util.List;

public class ResetVendorTrucksServiceHelper {

    public void resetVendorTrucks(Context context, @NonNull List<Truck> vendorTrucks) {
        // Nothing to do if the list is empty
        if (vendorTrucks.isEmpty()) {
            return;
        }

        String[] truckIds = new String[vendorTrucks.size()];

        for (int i = 0; i < vendorTrucks.size(); i++) {
            truckIds[i] = vendorTrucks.get(i).id;
        }

        Intent intent = ResetVendorTrucksService.newIntent(context, truckIds);
        context.startService(intent);
    }
}
