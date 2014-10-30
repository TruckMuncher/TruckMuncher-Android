package com.truckmuncher.truckmuncher.customer;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TruckCluster implements ClusterItem {
    private final LatLng position;

    public TruckCluster(double lat, double lng) {
        position = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}
