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

import com.squareup.picasso.Picasso;
import com.truckmuncher.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.guava.common.base.Preconditions.checkNotNull;

public class CustomerMenuFragment extends ListFragment implements TruckDataLoaderHandler.DataDestination {

    private static final String ARG_TRUCK_ID = "truck_id";
    @InjectView(R.id.truck_name)
    TextView truckName;
    @InjectView(R.id.truck_keywords)
    TextView truckKeywords;
    @InjectView(R.id.truck_image)
    ImageView truckImage;
    @InjectView(R.id.header)
    View headerView;
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
    public void onTruckDataLoaded(String name, String keywords, String imageUrl, String menuBackgroundColor, String headerColor) {
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
        }

        truckName.setText(name);
        truckKeywords.setText(keywords);

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
