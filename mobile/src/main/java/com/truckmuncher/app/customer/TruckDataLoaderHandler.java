package com.truckmuncher.app.customer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;

import timber.log.Timber;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

// TODO this class is way more complex than it needs to be at this point
final class TruckDataLoaderHandler implements LoaderManager.LoaderCallbacks<Cursor> {

    private final Context context;
    private final String truckId;
    private final DataDestination dataDestination;
    private final OnTriedToLoadInvalidTruckListener invalidTruckListener;

    TruckDataLoaderHandler(Context context, DataDestination dataDestination, String truckId, OnTriedToLoadInvalidTruckListener invalidTruckListener) {
        this.context = context;
        this.truckId = truckId;
        this.dataDestination = dataDestination;
        this.invalidTruckListener = invalidTruckListener;
    }

    public void load() {
        dataDestination.getLoaderManager().initLoader(DataDestination.LOADER_TRUCK, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DataDestination.LOADER_TRUCK: {
                WhereClause whereClause = new WhereClause.Builder()
                        .where(PublicContract.Truck.ID, EQUALS, truckId)
                        .build();
                return new CursorLoader(context, PublicContract.TRUCK_URI, TruckQuery.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
            }
            case DataDestination.LOADER_MENU: {
                WhereClause whereClause = new WhereClause.Builder()
                        .where(PublicContract.Menu.TRUCK_ID, EQUALS, truckId)
                        .build();
                String[] projection = MenuAdapter.Query.PROJECTION;
                Uri uri = Contract.syncFromNetwork(PublicContract.MENU_URI);
                return new CursorLoader(context, uri, projection, whereClause.selection, whereClause.selectionArgs, null);
            }
            default:
                throw new IllegalArgumentException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DataDestination.LOADER_TRUCK:
                onTruckDataLoaded(data);
                break;
            case DataDestination.LOADER_MENU:
                dataDestination.onMenuDataLoaded(data);
                break;
        }
    }

    private void onTruckDataLoaded(Cursor cursor) {
        if (cursor.moveToFirst()) {

            // Wait to load the menu until we have a truck so that we for sure have the category color
            dataDestination.getLoaderManager().initLoader(DataDestination.LOADER_MENU, null, this);

            String primaryColor = cursor.getString(TruckQuery.COLOR_PRIMARY);
            dataDestination.onTruckDataLoaded(primaryColor);
        } else {

            // Invalid truck
            Timber.w("Tried to load an invalid truck with id %s", truckId);
            invalidTruckListener.onTriedToLoadInvalidTruck();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DataDestination.LOADER_MENU:
                dataDestination.onMenuDataLoaded(null);
                break;
        }
    }

    interface DataDestination {
        int LOADER_TRUCK = 0;
        int LOADER_MENU = 1;

        void onTruckDataLoaded(String menuBackgroundColor);

        void onMenuDataLoaded(Cursor data);

        LoaderManager getLoaderManager();
    }

    interface TruckQuery {
        String[] PROJECTION = new String[]{
                PublicContract.Truck.COLOR_PRIMARY
        };
        int COLOR_PRIMARY = 0;
    }

    public interface OnTriedToLoadInvalidTruckListener {
        void onTriedToLoadInvalidTruck();
    }
}
