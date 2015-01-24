package com.truckmuncher.app.customer;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.truckmuncher.api.trucks.Truck;

public class TruckCluster implements ClusterItem {
    private final LatLng position;
    private Truck truck;

    public TruckCluster(Truck truck, LatLng position) {
        this.truck = truck;
        this.position = position;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public Truck getTruck() {
        return truck;
    }
}
