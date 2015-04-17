package com.truckmuncher.app.customer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.truckmuncher.app.R;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AllTrucksFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, GridView.OnItemClickListener {

    private static final int REQUEST_TRUCK_DETAILS = 0;

    @InjectView(R.id.all_trucks_grid)
    GridView gridView;

    private TrucksGridAdapter gridAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_trucks, container, false);
        ButterKnife.inject(this, view);

        gridAdapter = new TrucksGridAdapter(getActivity(), R.layout.grid_item_truck, null);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Contract.TRUCK_PROPERTIES_URI, TrucksGridAdapter.TruckQuery.PROJECTION,
                null, new String[]{}, PublicContract.Truck.NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        gridAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        // No-op
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String currentTruck = gridAdapter.getTruckId(i);
        ArrayList<String> truckIds = gridAdapter.getTruckIds();
        startActivityForResult(TruckDetailsActivity.newIntent(getActivity(), truckIds, currentTruck), REQUEST_TRUCK_DETAILS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TRUCK_DETAILS:
                if (resultCode == Activity.RESULT_OK) {
                    String lastTruckId = data.getStringExtra(TruckDetailsActivity.ARG_ENDING_TRUCK);
                    gridView.smoothScrollToPosition(gridAdapter.getTruckPosition(lastTruckId));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
