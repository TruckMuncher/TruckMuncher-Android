package com.truckmuncher.truckmuncher.customer;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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
import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.PublicContract;
import com.truckmuncher.truckmuncher.data.sql.WhereClause;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.truckmuncher.truckmuncher.data.sql.WhereClause.Operator.EQUALS;

public class CustomerMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ClusterManager.OnClusterClickListener<TruckCluster>, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_MAP_STATE = "map_state";

    @InjectView(R.id.customer_map)
    MapView mapView;

    GoogleApiClient apiClient;
    LatLng currentLocation;
    ClusterManager<TruckCluster> clusterManager;
    private ClusterRenderer<TruckCluster> renderer;
    private Map<String, TruckCluster> markers = Collections.emptyMap();

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

        mapView.getMap().getUiSettings().setZoomControlsEnabled(true);

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
            loadActiveTrucks(null);
            getActivity().startService(GetTruckProfilesService.newIntent(getActivity(), currentLocation.latitude, currentLocation.longitude));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO Consider handling
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Selection args for trucks that are currently in serving mode
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.IS_SERVING, EQUALS, true)
                .build();
        return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, ActiveTrucksQuery.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToPosition(-1);

        markers = new HashMap<>();

        while (cursor.moveToNext()) {
            Truck truck = new Truck.Builder()
                    .id(cursor.getString(ActiveTrucksQuery.ID))
                    .name(cursor.getString(ActiveTrucksQuery.NAME))
                    .build();

            LatLng location = new LatLng(cursor.getDouble(ActiveTrucksQuery.LATITUDE),
                    cursor.getDouble(ActiveTrucksQuery.LONGITUDE));

            markers.put(truck.id, new TruckCluster(truck, location));
        }

        if (clusterManager != null) {
            clusterManager.clearItems();
            clusterManager.addItems(markers.values());
            clusterManager.setRenderer(renderer);
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

        // true to prevent the default behavior from occurring
        return true;
    }

    public void loadActiveTrucks(String searchQuery) {

        // Kick off a refresh of the vendor data
        getActivity().startService(ActiveTrucksService.newIntent(getActivity(), currentLocation.latitude, currentLocation.longitude, searchQuery));
    }

    private void setUpClusterer() {
        GoogleMap map = mapView.getMap();
        // Initialize the manager with the context and the map.
        clusterManager = new ClusterManager<>(getActivity(), map);

        renderer = new TruckClusterRenderer<>(getActivity(), map, clusterManager);
        clusterManager.setRenderer(renderer);

        clusterManager.setOnClusterClickListener(this);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    public void moveTo(String truckId) {
        TruckCluster cluster = markers.get(truckId);
        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
    }

    public interface ActiveTrucksQuery {

        public static final String[] PROJECTION = new String[]{
                PublicContract.Truck.ID,
                PublicContract.Truck.LATITUDE,
                PublicContract.Truck.LONGITUDE,
                PublicContract.Truck.NAME
        };
        static final int ID = 0;
        static final int LATITUDE = 1;
        static final int LONGITUDE = 2;
        static final int NAME = 3;
    }
}
