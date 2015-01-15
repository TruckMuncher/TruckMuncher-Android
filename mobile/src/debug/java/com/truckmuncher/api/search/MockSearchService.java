package com.truckmuncher.api.search;

import com.truckmuncher.api.menu.Category;
import com.truckmuncher.api.menu.Menu;
import com.truckmuncher.api.trucks.Truck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.http.Body;

public class MockSearchService implements SearchService {
    @Override
    public SimpleSearchResponse simpleSearch(@Body SimpleSearchRequest request) {
        List<SearchResponse> searchResponseList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Truck truck = new Truck.Builder()
                    .id("Truck" + i)
                    .imageUrl("http://api.truckmuncher.com/images/truck/" + i)
                    .name("Truck" + i)
                    .keywords(Arrays.asList("These", "Are", "Keywords"))
                    .primaryColor("#0000FF")
                    .secondaryColor("#FF0000")
                    .build();

            Menu menu = new Menu.Builder()
                    .truckId(truck.id)
                    .categories(new ArrayList<Category>())
                    .build();


            SearchResponse response = new SearchResponse.Builder()
                    .truck(truck)
                    .blurb("<" + truck.name + ">")
                    .menu(menu)
                    .build();

            searchResponseList.add(response);
        }

        return new SimpleSearchResponse(searchResponseList);
    }
}
