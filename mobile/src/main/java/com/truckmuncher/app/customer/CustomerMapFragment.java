package com.truckmuncher.app.customer;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
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
import com.truckmuncher.app.ApiClientFragment;
import com.truckmuncher.app.R;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class CustomerMapFragment extends ApiClientFragment implements
        ClusterManager.OnClusterClickListener<TruckCluster>,
        ClusterManager.OnClusterItemClickListener<TruckCluster>, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_MAP_STATE = "map_state";

    private static final int REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private static final int FASTEST_REFRESH_INTERVAL = 1 * 60 * 1000; // 1 minute

    @InjectView(R.id.customer_map)
    MapView mapView;

    LatLng currentLocation;
    ClusterManager<TruckCluster> clusterManager;
    private Map<String, TruckCluster> activeTruckMarkers = Collections.emptyMap();
    private SimpleSearchServiceHelper serviceHelper;
    private OnTruckMarkerClickListener onTruckMarkerClickListener;
    private OnLocationChangeListener onLocationChangeListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTruckMarkerClickListener = (OnTruckMarkerClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement " + OnTruckMarkerClickListener.class.getName());
        }
        try {
            onLocationChangeListener = (OnLocationChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement " + OnLocationChangeListener.class.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceHelper = new SimpleSearchServiceHelper();

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
    public void onPause() {
        super.onPause();
        mapView.onPause();
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
                .setFastestInterval(FASTEST_REFRESH_INTERVAL)
                .setInterval(REFRESH_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // if the current location is null, we haven't loaded the active trucks yet.
        boolean trucksNeedLoading = currentLocation == null;

        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (trucksNeedLoading) {
            loadActiveTrucks();
            getActivity().startService(GetTruckProfilesService.newIntent(getActivity(), currentLocation.latitude, currentLocation.longitude));
        }

        onLocationChangeListener.onLocationChange((currentLocation));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Selection args for trucks that are currently in serving mode
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.IS_SERVING, EQUALS, true)
                .and()
                .where(PublicContract.Truck.MATCHED_SEARCH, EQUALS, true)
                .build();
        return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, ActiveTrucksQuery.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToPosition(-1);

        activeTruckMarkers = new HashMap<>();

        while (cursor.moveToNext()) {
            String truckId = cursor.getString(ActiveTrucksQuery.ID);

            LatLng location = new LatLng(cursor.getDouble(ActiveTrucksQuery.LATITUDE),
                    cursor.getDouble(ActiveTrucksQuery.LONGITUDE));

            activeTruckMarkers.put(truckId, new TruckCluster(truckId, location));
        }

        forceClusterRender(activeTruckMarkers.values());
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

    public void searchTrucks(final String query) {
        if (query == null || query.isEmpty()) {
            serviceHelper.clearSearchQueryMatches(getActivity());
        } else {
            getActivity().startService(SimpleSearchService.newIntent(getActivity(), query));
        }
    }

    @Override
    public boolean onClusterItemClick(final TruckCluster truckClusterItem) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                onTruckMarkerClickListener.onTruckMarkerClick(truckClusterItem);
            }
        });

        t.start();

        // false to preserve the default behavior of centering the screen on the marker
        return false;
    }

    private void loadActiveTrucks() {
        // Kick off a refresh of the vendor data
        getActivity().startService(ActiveTrucksService.newIntent(getActivity(), currentLocation.latitude, currentLocation.longitude));
    }

    private void setUpClusterer() {
        GoogleMap map = mapView.getMap();
        // Initialize the manager with the context and the map.
        clusterManager = new ClusterManager<>(getActivity(), map);

        ClusterRenderer<TruckCluster> renderer = new TruckClusterRenderer<>(getActivity(), map, clusterManager);
        clusterManager.setRenderer(renderer);

        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    private void forceClusterRender(Collection<TruckCluster> markers) {
        if (clusterManager != null) {
            clusterManager.clearItems();
            clusterManager.addItems(markers);
            clusterManager.cluster();
        }
    }

    public void moveTo(String truckId) {
        TruckCluster cluster = activeTruckMarkers.get(truckId);

        if (cluster != null) {
            mapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
        }
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public interface ActiveTrucksQuery {

        public static final String[] PROJECTION = new String[]{
                PublicContract.Truck.ID,
                PublicContract.Truck.LATITUDE,
                PublicContract.Truck.LONGITUDE
        };
        static final int ID = 0;
        static final int LATITUDE = 1;
        static final int LONGITUDE = 2;
    }

    public interface OnTruckMarkerClickListener {
        public void onTruckMarkerClick(TruckCluster truckClusterItem);
    }

    public interface OnLocationChangeListener {
        public void onLocationChange(LatLng location);
    }
}
