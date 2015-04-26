package com.truckmuncher.app.customer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class TruckClusterRendererTest {

    private TruckClusterRenderer<TruckCluster> clusterRenderer;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getContext();

        GoogleMap map = new MapView(context).getMap();

        ClusterManager<TruckCluster> clusterManager = new ClusterManager<>(context, map);
        clusterRenderer = new TruckClusterRenderer<>(context, map, clusterManager);
    }

    @Test
    public void shouldRenderAsCluster() {
        Cluster<TruckCluster> cluster = mock(Cluster.class);

        when(cluster.getSize())
                .thenReturn(1)
                .thenReturn(2);

        // Should not render as cluster with only 1 item
        assertThat(clusterRenderer.shouldRenderAsCluster(cluster)).isEqualTo(false);

        // Should render as cluster with 2 or more items
        assertThat(clusterRenderer.shouldRenderAsCluster(cluster)).isEqualTo(true);

    }
}
