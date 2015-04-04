package com.truckmuncher.app.customer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.search.SearchResponse;
import com.truckmuncher.api.search.SearchService;
import com.truckmuncher.api.search.SimpleSearchRequest;
import com.truckmuncher.api.search.SimpleSearchResponse;
import com.truckmuncher.app.App;
import com.truckmuncher.app.data.ApiException;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class SimpleSearchService extends IntentService {

    public static final String ARG_SEARCH_QUERY = "search_query";
    public static final String ARG_MESSAGE = "user_message";

    @Inject
    SearchService searchService;

    private SimpleSearchServiceHelper serviceHelper;

    public SimpleSearchService() {
        super(SimpleSearchService.class.getSimpleName());

        serviceHelper = new SimpleSearchServiceHelper();
    }

    public static Intent newIntent(Context context, String searchQuery) {
        Intent intent = new Intent(context, SimpleSearchService.class);
        intent.putExtra(ARG_SEARCH_QUERY, searchQuery);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String searchQuery = intent.getStringExtra(ARG_SEARCH_QUERY);

        SimpleSearchRequest request = new SimpleSearchRequest.Builder()
                .query(searchQuery)
                .build();

        try {
            SimpleSearchResponse response = searchService.simpleSearch(request);

            List<SearchResponse> searchResponses = response.searchResponse;
            String[] truckIds = new String[searchResponses.size()];

            for (int i = 0, max = searchResponses.size(); i < max; i++) {
                truckIds[i] = searchResponses.get(i).truck.id;
            }

            serviceHelper.setSearchQueryMatches(this, truckIds);
        } catch (ApiException e) {
            Timber.e("Got an error while performing a trucks search.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(SimpleSearchService.this).sendBroadcast(errorIntent);
        }
    }
}
