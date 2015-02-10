package com.truckmuncher.app.customer;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.truckmuncher.app.R;

public class TruckClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {

    public TruckClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
        //start clustering if at least 2 items overlap
        return cluster.getSize() > 1;
    }

    @Override
    protected void onBeforeClusterItemRendered(T item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));
    }
}
