package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.truckmuncher.app.R;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.guava.common.base.Preconditions.checkNotNull;

public class CustomerMenuFragment extends ListFragment implements TruckDataLoaderHandler.DataDestination {

    private static final double METERS_TO_MILES = 0.000621371;
    
    private static final String ARG_TRUCK_ID = "truck_id";
    private static final String ARG_LOCATION = "location";
    private static final int LOADER_TRUCK = 0;
    private static final int LOADER_MENU = 1;
    @InjectView(R.id.truck_name)
    TextView truckName;
    @InjectView(R.id.truck_keywords)
    TextView truckKeywords;
    @InjectView(R.id.distance_from_location)
    TextView distanceFromLocation;
    @InjectView(R.id.truck_image)
    ImageView truckImage;
    @InjectView(R.id.header)
    View headerView;
    private MenuAdapter adapter;
    private String menuBackgroundColor;

    public static CustomerMenuFragment newInstance(@NonNull String truckId, LatLng referenceLocation) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, checkNotNull(truckId));
        if (referenceLocation != null) {
            args.putParcelable(ARG_LOCATION, referenceLocation);
        }
        CustomerMenuFragment fragment = new CustomerMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_menu, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        getListView().setFastScrollEnabled(true);
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
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onTruckDataLoaded(String name, String keywords, String imageUrl, String menuBackgroundColor, String headerColor, LatLng truckLocation) {
        if (TextUtils.isEmpty(imageUrl)) {
            truckImage.setVisibility(View.GONE);
        } else {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .fit()
                    .centerInside()
                    .into(truckImage);
        }

        if (headerColor != null) {
            headerView.setBackgroundColor(Color.parseColor(headerColor));
            int textColor = ColorCorrector.calculateTextColor(headerColor);
            truckName.setTextColor(textColor);
            truckKeywords.setTextColor(textColor);
            distanceFromLocation.setTextColor(textColor);
        }

        truckName.setText(name);
        truckKeywords.setText(keywords);

        if (menuBackgroundColor != null) {
            this.menuBackgroundColor = menuBackgroundColor;
            getListView().setBackgroundColor(Color.parseColor(menuBackgroundColor));
        }

        LatLng referenceLocation = getArguments().getParcelable(ARG_LOCATION);

        // distance in meters
        double delta = SphericalUtil.computeDistanceBetween(truckLocation, referenceLocation);
        // convert to miles
        delta *= METERS_TO_MILES;

        distanceFromLocation.setText(new DecimalFormat("0.0").format(delta) + " mi");
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
