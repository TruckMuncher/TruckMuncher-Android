package com.truckmuncher.app.customer;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.truckmuncher.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class TruckListFragment extends Fragment {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.view_pager)
    ViewPager pager;

    private TabsPagerAdapter adapter;
    private float elevation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truck_list, container, false);
        ButterKnife.inject(this, view);

        // Initialize the ViewPager and set an adapter
        adapter = new TabsPagerAdapter(getActivity(), getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        setElevations();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setElevation(elevation);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setElevations() {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        elevation = actionBar.getElevation();
        tabs.setElevation(elevation);
        actionBar.setElevation(0);
    }
}
