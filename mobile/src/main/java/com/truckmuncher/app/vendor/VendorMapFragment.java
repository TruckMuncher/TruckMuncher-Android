package com.truckmuncher.app.vendor;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.truckmuncher.app.ApiClientFragment;
import com.truckmuncher.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTouch;

public abstract class VendorMapFragment extends ApiClientFragment {

    private static final String ARG_MAP_STATE = "map_state";

    private static final int REFRESH_INTERVAL = 30 * 1000; // 30 seconds
    private static final int FASTEST_REFRESH_INTERVAL = 10 * 1000; // 10 seconds

    @InjectView(R.id.map)
    MapView mapView;

    private boolean useMapLocation;
    private LocationRequest request;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        final GoogleMap map = mapView.getMap();

        // Configure map
        map.setMyLocationEnabled(true);

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                useMapLocation = false;
                map.clear();
                return false;
            }
        });

        Location myLocation = map.getMyLocation();
        final LatLng latLng;
        if (myLocation != null) {
            latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mapView.onDestroy();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        mapView.onSaveInstanceState(mapState);
        outState.putParcelable(ARG_MAP_STATE, mapState);
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @OnTouch(R.id.mapGlass)
    boolean onMapTouch(View view) {
        useMapLocation = true;

        // TODO See if there is a better way to determine when the user has dragged the map
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng target = mapView.getMap().getCameraPosition().target;
                Location location = new Location("");
                location.setLongitude(target.longitude);
                location.setLatitude(target.latitude);
                onLocationUpdate(location);
            }
        }, 1000);

        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!useMapLocation) {
            Location myLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            if (myLocation != null) {
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                MapsInitializer.initialize(getActivity());
                mapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
                onLocationUpdate(myLocation);
            }
        }

        request = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(FASTEST_REFRESH_INTERVAL)
                .setInterval(REFRESH_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
    }

    @Override
    public final void onLocationChanged(Location location) {
        if (!useMapLocation) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
            onLocationUpdate(location);
        }
    }

    public void setMapControlsEnabled(boolean enabled) {
        UiSettings settings = mapView.getMap().getUiSettings();
        settings.setScrollGesturesEnabled(enabled);
        settings.setRotateGesturesEnabled(enabled);
        settings.setZoomControlsEnabled(enabled);
        settings.setZoomGesturesEnabled(enabled);
        settings.setMyLocationButtonEnabled(enabled);

        if (enabled) {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
        } else {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }

    }

    abstract void onLocationUpdate(Location location);
}
