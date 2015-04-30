package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.truckmuncher.app.data.PublicContract;

public class AllTrucksGridFragment extends BaseGridFragment {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderBy = new StringBuilder()
                .append(PublicContract.Truck.IS_SERVING)
                .append(" DESC, ")
                .append(PublicContract.Truck.NAME)
                .append(" ASC")
                .toString();

        return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, TrucksGridAdapter.TruckQuery.PROJECTION,
                null, new String[]{}, orderBy);
    }
}
