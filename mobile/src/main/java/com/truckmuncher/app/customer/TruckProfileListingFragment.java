package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.SimpleCursorAdapter;

import com.truckmuncher.app.data.PublicContract;

public class TruckProfileListingFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, Query.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adapter == null) {
            adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, data, new String[]{PublicContract.Truck.NAME}, new int[]{android.R.id.text1}, 0);
            setListAdapter(adapter);
        }
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    interface Query {
        String[] PROJECTION = {
                PublicContract.Truck._ID,
                PublicContract.Truck.NAME,
                PublicContract.Truck.DESCRIPTION,
                PublicContract.Truck.IMAGE_URL
        };
    }
}
