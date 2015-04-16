package com.truckmuncher.app.customer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.truckmuncher.app.R;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AllTrucksActivity extends ActionBarActivity 
        implements LoaderManager.LoaderCallbacks<Cursor>, GridView.OnItemClickListener {

    private static final int REQUEST_TRUCK_DETAILS = 0;
    
    @InjectView(R.id.all_trucks_grid)
    GridView gridView;

    private TrucksGridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trucks);
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridAdapter = new TrucksGridAdapter(this, R.layout.grid_item_truck, null);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.TRUCK_PROPERTIES_URI, TrucksGridAdapter.TruckQuery.PROJECTION,
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
        startActivityForResult(TruckDetailsActivity.newIntent(this, truckIds, currentTruck), REQUEST_TRUCK_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TRUCK_DETAILS:
                if (resultCode == RESULT_OK) {
                    String lastTruckId = data.getStringExtra(TruckDetailsActivity.ARG_ENDING_TRUCK);
                    gridView.smoothScrollToPosition(gridAdapter.getTruckPosition(lastTruckId));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
