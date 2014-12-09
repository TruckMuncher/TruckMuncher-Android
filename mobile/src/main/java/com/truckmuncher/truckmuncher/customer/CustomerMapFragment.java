package com.truckmuncher.truckmuncher.customer;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.truckmuncher.ActiveTrucksService;
import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.util.CursorLoaderFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ClusterManager.OnClusterClickListener<TruckCluster>, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_MAP_STATE = "map_state";

    @InjectView(R.id.customer_map)
    MapView mapView;

    GoogleApiClient apiClient;
    LatLng currentLocation;
    ClusterManager<TruckCluster> clusterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_map, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            Bundle mapState = savedInstanceState.getBundle(ARG_MAP_STATE);
            mapView.onCreate(mapState);
        } else {
            mapView.onCreate(null);
        }

        apiClient = new GoogleApiClient.Builder(getActivity(), this, this)
                .addApi(LocationServices.API)
                .build();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        GoogleMap map = mapView.getMap();

        if (map != null) {
            map.setMyLocationEnabled(true);
            setUpClusterer();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
            apiClient.disconnect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        mapView.onSaveInstanceState(mapState);
        outState.putParcelable(ARG_MAP_STATE, mapState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (myLocation != null) {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            MapsInitializer.initialize(getActivity());
            mapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        LocationRequest request = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(5000)
                .setInterval(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // if the current location is null, we haven't loaded the active trucks yet.
        boolean trucksNeedLoading = currentLocation == null;

        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (trucksNeedLoading) {
            loadActiveTrucks();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO Consider handling
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Uri for trucks that are currently in serving mode
        Uri uri = Contract.TruckEntry.buildServingTrucks();

        return CursorLoaderFactory.create(getActivity(), uri, ActiveTrucksQuery.PROJECTION);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToPosition(-1);

        List<TruckCluster> markers = new ArrayList<>();

        while (cursor.moveToNext()) {
            Truck truck = new Truck.Builder()
                    .id(cursor.getString(ActiveTrucksQuery.INTERNAL_ID))
                    .name(cursor.getString(ActiveTrucksQuery.NAME))
                    .build();

            LatLng location = new LatLng(cursor.getDouble(ActiveTrucksQuery.LATITUDE),
                    cursor.getDouble(ActiveTrucksQuery.LONGITUDE));

            markers.add(new TruckCluster(truck, location));
        }

        if (clusterManager != null) {
            clusterManager.clearItems();
            clusterManager.addItems(markers);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // No-op
    }

    @Override
    public boolean onClusterClick(Cluster<TruckCluster> cluster) {
        float currentZoom = mapView.getMap().getCameraPosition().zoom;
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), currentZoom + 1));

        // true to prevent the default behavior from occuring
        return true;
    }

    private void setUpClusterer() {
        GoogleMap map = mapView.getMap();
        // Initialize the manager with the context and the map.
        clusterManager = new ClusterManager<>(getActivity(), map);

        ClusterRenderer<TruckCluster> renderer = new TruckClusterRenderer<>(getActivity(), map, clusterManager);
        clusterManager.setRenderer(renderer);

        clusterManager.setOnClusterClickListener(this);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    private void loadActiveTrucks() {
        // Kick off a refresh of the vendor data
        Intent intent = new Intent(getActivity(), ActiveTrucksService.class);
        intent.putExtra(ActiveTrucksService.ARG_LATITUDE, currentLocation.latitude);
        intent.putExtra(ActiveTrucksService.ARG_LONGITUDE, currentLocation.longitude);
        getActivity().startService(intent);
    }

    public interface ActiveTrucksQuery {

        public static final String[] PROJECTION = new String[]{
                Contract.TruckEntry.COLUMN_INTERNAL_ID,
                Contract.TruckEntry.COLUMN_LATITUDE,
                Contract.TruckEntry.COLUMN_LONGITUDE,
                Contract.TruckEntry.COLUMN_NAME
        };
        static final int INTERNAL_ID = 0;
        static final int LATITUDE = 1;
        static final int LONGITUDE = 2;
        static final int NAME = 3;
    }
}
