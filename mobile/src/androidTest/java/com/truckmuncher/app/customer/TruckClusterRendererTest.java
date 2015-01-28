package com.truckmuncher.app.customer;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TruckClusterRendererTest extends AndroidTestCase {

    private TruckClusterRenderer<TruckCluster> clusterRenderer;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        GoogleMap map = new MapView(mContext).getMap();

        ClusterManager clusterManager = new ClusterManager<>(mContext, map);
        clusterRenderer = new TruckClusterRenderer<TruckCluster>(mContext, map, clusterManager);
    }

    public void testShouldRenderAsCluster() {
        Cluster cluster = mock(Cluster.class);

        when(cluster.getSize())
                .thenReturn(1)
                .thenReturn(2);

        // Should not render as cluster with only 1 item
        assertThat(clusterRenderer.shouldRenderAsCluster(cluster)).isEqualTo(false);

        // Should render as cluster with 2 or more items
        assertThat(clusterRenderer.shouldRenderAsCluster(cluster)).isEqualTo(true);

    }
}
