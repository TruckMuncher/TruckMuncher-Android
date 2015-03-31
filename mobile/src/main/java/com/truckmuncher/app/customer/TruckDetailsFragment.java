package com.truckmuncher.app.customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.truckmuncher.app.R;

import static com.guava.common.base.Preconditions.checkNotNull;

public class TruckDetailsFragment extends Fragment {

    private static final String ARG_TRUCK_ID = "truck_id";

    public static TruckDetailsFragment newInstance(@NonNull String truckId) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, checkNotNull(truckId));
        TruckDetailsFragment fragment = new TruckDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_truck_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String truckId = getArguments().getString(ARG_TRUCK_ID);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header, TruckHeaderFragment.newInstance(truckId, null))
                .replace(R.id.menu, CustomerMenuFragment.newInstance(truckId))
                .commit();
    }
}
