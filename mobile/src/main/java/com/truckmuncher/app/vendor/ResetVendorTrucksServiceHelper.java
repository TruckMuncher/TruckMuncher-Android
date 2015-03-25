package com.truckmuncher.app.vendor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.truckmuncher.api.trucks.Truck;

import java.util.List;

public class ResetVendorTrucksServiceHelper {

    public void resetVendorTrucks(Context context, String[] truckIds) {
        // Nothing to do if the list is empty
        if (truckIds == null || truckIds.length == 0) {
            return;
        }

        Intent intent = ResetVendorTrucksService.newIntent(context, truckIds);
        context.startService(intent);
    }
}
