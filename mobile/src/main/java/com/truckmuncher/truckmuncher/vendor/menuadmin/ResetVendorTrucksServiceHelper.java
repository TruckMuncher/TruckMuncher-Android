package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;

import java.util.List;

public class ResetVendorTrucksServiceHelper {

    public void resetVendorTrucks(Context context, @NonNull List<Truck> vendorTrucks) {
        // Nothing to do if the list is empty
        if (vendorTrucks.isEmpty()) {
            return;
        }

        ContentValues[] contentValues = new ContentValues[vendorTrucks.size()];

        for (int i = 0; i < contentValues.length; i++) {
            Truck truck = vendorTrucks.get(i);
            ContentValues values = new ContentValues();
            values.put(PublicContract.Truck.ID, truck.id);
            values.put(PublicContract.Truck.NAME, truck.name);
            values.put(PublicContract.Truck.IMAGE_URL, truck.imageUrl);
            values.put(PublicContract.Truck.KEYWORDS, Contract.convertListToString(truck.keywords));
            values.put(PublicContract.Truck.COLOR_PRIMARY, truck.primaryColor);
            values.put(PublicContract.Truck.COLOR_SECONDARY, truck.secondaryColor);
            values.put(PublicContract.Truck.OWNED_BY_CURRENT_USER, 0);
            contentValues[i] = values;
        }

        Intent intent = ResetVendorTrucksService.newIntent(context, contentValues);
        context.startService(intent);
    }
}
