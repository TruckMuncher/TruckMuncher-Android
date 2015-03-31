package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.View;

import static com.guava.common.base.Preconditions.checkNotNull;

public class CustomerMenuFragment extends ListFragment implements TruckDataLoaderHandler.DataDestination {

    private static final String ARG_TRUCK_ID = "truck_id";
    private MenuAdapter adapter;
    private String menuBackgroundColor;

    public static CustomerMenuFragment newInstance(@NonNull String truckId) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, checkNotNull(truckId));
        CustomerMenuFragment fragment = new CustomerMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        getListView().setFastScrollEnabled(true);

        // TODO probably not needed since this is used in a standalone activity now
        getListView().setBackgroundColor(getResources().getColor(android.R.color.background_light));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TruckDataLoaderHandler loaderHandler =
                new TruckDataLoaderHandler(getActivity(), this, getArguments().getString(ARG_TRUCK_ID), (TruckDataLoaderHandler.OnTriedToLoadInvalidTruckListener) getActivity());
        loaderHandler.load();
    }

    @Override
    public void onTruckDataLoaded(String menuBackgroundColor) {
        if (menuBackgroundColor != null) {
            this.menuBackgroundColor = menuBackgroundColor;
            getListView().setBackgroundColor(Color.parseColor(menuBackgroundColor));
        }
    }

    @Override
    public void onMenuDataLoaded(Cursor data) {
        if (adapter == null) {
            int textColor;
            if (menuBackgroundColor != null) {
                textColor = ColorCorrector.calculateTextColor(menuBackgroundColor);
            } else {
                textColor = Color.BLACK;
            }
            adapter = new MenuAdapter(getActivity(), textColor);
            setListAdapter(adapter);
        }
        adapter.swapCursor(data);
    }
}
