package com.truckmuncher.app.customer;

import com.google.android.gms.maps.model.LatLng;
import com.truckmuncher.testlib.ReadableRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRunner.class)
public class TruckClusterTest {

    @Test
    public void getTruckIdReturnsIdFromConstructor() {
        String id = "id";
        TruckCluster cluster = new TruckCluster("id", null);
        assertThat(cluster.getTruckId()).isSameAs(id);
    }

    @Test
    public void getPositionReturnsPositionFromConstructor() {
        LatLng location = new LatLng(43.2, 80.5);
        TruckCluster cluster = new TruckCluster(null, location);
        assertThat(cluster.getPosition()).isSameAs(location);
    }
}
