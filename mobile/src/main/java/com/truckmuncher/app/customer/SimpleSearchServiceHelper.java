package com.truckmuncher.app.customer;

import android.content.Context;
import android.os.Bundle;

import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.TruckMuncherContentProvider;

public class SimpleSearchServiceHelper {

    void setSearchQueryMatches(Context context, String[] truckIds) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(TruckMuncherContentProvider.ARG_ID_ARRAY, truckIds);

        context.getContentResolver().call(Contract.TRUCK_STATE_URI,
                TruckMuncherContentProvider.METHOD_UPDATE_SEARCH_RESULTS, null, bundle);
    }

    void clearSearchQueryMatches(Context context) {
        context.getContentResolver().call(Contract.TRUCK_STATE_URI,
                TruckMuncherContentProvider.METHOD_CLEAR_SEARCH_RESULTS, null, null);
    }
}
