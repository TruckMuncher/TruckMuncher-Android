package com.truckmuncher.truckmuncher.vendor;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.search.SearchResponse;
import com.truckmuncher.api.search.SearchService;
import com.truckmuncher.api.search.SimpleSearchRequest;
import com.truckmuncher.api.search.SimpleSearchResponse;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.truckmuncher.App;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.Contract;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class SimpleSearchService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";
    public static final String ARG_SEARCH_QUERY = "search_query";

    @Inject
    SearchService searchService;

    public SimpleSearchService() {
        super(SearchService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        App.inject(this, this);
//
//        String searchQuery = intent.getStringExtra(ARG_SEARCH_QUERY);
//
//        SimpleSearchRequest request = new SimpleSearchRequest(searchQuery, null, null);
//
//        try {
//            SimpleSearchResponse response = searchService.simpleSearch(request);
//
//            List<SearchResponse> searchResponses = response.searchResponse;
//            ContentValues[] contentValues = new ContentValues[searchResponses.size()];
//            for (int i = 0, max = searchResponses.size(); i < max; i++) {
//                Truck truck = searchResponses.get(i).truck;
//                ContentValues values = new ContentValues();
//                values.put(Contract.TruckStateEntry.COLUMN_INTERNAL_ID, truck.id);
//                values.put(Contract.TruckStateEntry.COLUMN_LATITUDE, truck.latitude);
//                values.put(Contract.TruckStateEntry.COLUMN_LONGITUDE, truck.longitude);
//                values.put(Contract.TruckStateEntry.COLUMN_IS_SERVING, 1);
//                contentValues[i] = values;
//            }
//
//            getContentResolver().delete(Contract.TruckStateEntry.CONTENT_URI, null, null);
//            getContentResolver().bulkInsert(Contract.TruckStateEntry.CONTENT_URI, contentValues);
//        } catch (ApiException e) {
//            Timber.e("Got an error while getting active trucks.");
//            Intent errorIntent = new Intent();
//            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
//            LocalBroadcastManager.getInstance(SimpleSearchService.this).sendBroadcast(errorIntent);
//        }
    }
}
