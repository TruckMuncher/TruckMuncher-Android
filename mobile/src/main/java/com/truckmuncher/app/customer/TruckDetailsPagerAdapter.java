package com.truckmuncher.app.customer;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class TruckDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private final List<String> truckIds;

    public TruckDetailsPagerAdapter(FragmentManager fm, List<String> truckIds) {
        super(fm);
        this.truckIds = truckIds;
    }

    @Override
    public TruckDetailsFragment getItem(int i) {
        return TruckDetailsFragment.newInstance(truckIds.get(i));
    }

    @Override
    public int getCount() {
        return truckIds.size();
    }

    public String getTruckId(int position) {
        return truckIds.get(position);
    }

    public int getTruckPosition(String truckId) {
        return truckIds.indexOf(truckId);
    }
}
