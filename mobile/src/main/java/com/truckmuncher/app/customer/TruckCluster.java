package com.truckmuncher.app.customer;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public final class TruckCluster implements ClusterItem {
    private final LatLng position;
    private final String truckId;

    TruckCluster(String truckId, LatLng position) {
        this.truckId = truckId;
        this.position = position;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public String getTruckId() {
        return truckId;
    }
}
