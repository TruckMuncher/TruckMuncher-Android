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

import java.util.List;

import timber.log.Timber;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

final class CustomerMenuLoaderHandler implements LoaderManager.LoaderCallbacks<Cursor> {

    private final Context context;
    private final String truckId;
    private final DataDestination dataDestination;
    private final OnTriedToLoadInvalidTruckListener invalidTruckListener;

    CustomerMenuLoaderHandler(Context context, DataDestination dataDestination, String truckId, OnTriedToLoadInvalidTruckListener invalidTruckListener) {
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

            // Split the keywords and format them in a way that is user friendly
            String keywordsString = cursor.getString(TruckQuery.KEYWORDS);
            List<String> keywords = Contract.convertStringToList(keywordsString);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < keywords.size(); i++) {
                builder.append(keywords.get(i));
                if (i < keywords.size() - 1) {
                    builder.append(", ");
                }
            }

            String truckName = cursor.getString(TruckQuery.NAME);
            String truckKeywords = builder.toString();
            String imageUrl = cursor.getString(TruckQuery.IMAGE_URL);
            String primaryColor = cursor.getString(TruckQuery.COLOR_PRIMARY);
            String secondaryColor = cursor.getString(TruckQuery.COLOR_SECONDARY);
            dataDestination.onTruckDataLoaded(truckName, truckKeywords, imageUrl, primaryColor, secondaryColor);
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

        void onTruckDataLoaded(String truckName, String keywords, String imageUrl, String menuBackgroundColor, String headerColor);

        void onMenuDataLoaded(Cursor data);

        LoaderManager getLoaderManager();
    }

    interface TruckQuery {
        String[] PROJECTION = new String[]{
                PublicContract.Truck.NAME,
                PublicContract.Truck.IMAGE_URL,
                PublicContract.Truck.KEYWORDS,
                PublicContract.Truck.COLOR_PRIMARY,
                PublicContract.Truck.COLOR_SECONDARY
        };
        int NAME = 0;
        int IMAGE_URL = 1;
        int KEYWORDS = 2;
        int COLOR_PRIMARY = 3;
        int COLOR_SECONDARY = 4;
    }

    public interface OnTriedToLoadInvalidTruckListener {
        void onTriedToLoadInvalidTruck();
    }
}
