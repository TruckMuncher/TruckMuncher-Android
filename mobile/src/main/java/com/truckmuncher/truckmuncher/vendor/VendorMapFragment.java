package com.truckmuncher.truckmuncher.vendor;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.truckmuncher.truckmuncher.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTouch;

public class VendorMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String ARG_MAP_STATE = "map_state";

    @InjectView(R.id.map)
    MapView mapView;

    private boolean useMapLocation;
    private OnMapLocationChangedListener onMapLocationChangedListener;
    private GoogleApiClient apiClient;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onMapLocationChangedListener = (OnMapLocationChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement " + OnMapLocationChangedListener.class.getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vendor_map, container, false);
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
        super.onViewCreated(view, savedInstanceState);

        final GoogleMap map = mapView.getMap();

        // Configure map
        int mapPaddingTop = getResources().getDimensionPixelOffset(R.dimen.vendor_hud_top_height);
        int mapPaddingBottom = getResources().getDimensionPixelOffset(R.dimen.vendor_hud_bottom_height);
        map.setPadding(0, mapPaddingTop, 0, mapPaddingBottom);
        map.setMyLocationEnabled(true);

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                useMapLocation = false;
                map.clear();
                return false;
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (useMapLocation) {
                    LatLng latLng = cameraPosition.target;
                    Location location = new Location("");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    // TODO this implementation of the pin is janky. We should have a static icon centered over the map so that it's smoother
                    map.clear();
                    map.addMarker(new MarkerOptions().position(latLng));
                }
            }
        });

        Location myLocation = map.getMyLocation();
        final LatLng latLng;
        if (myLocation != null) {
            latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        mapView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                onMapLocationChangedListener.onMapLocationChanged(latLng);
            }
        }, 500);    // Need a delayed post so fragments have time to setup
    }

    @Override
    public void onStart() {
        super.onStart();
        apiClient.connect();
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
    public void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        apiClient.disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onMapLocationChangedListener = null;
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

    @OnTouch(R.id.mapGlass)
    boolean onMapTouch(View view) {
        useMapLocation = true;

        // TODO See if there is a better way to determine when the user has dragged the map
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng target = mapView.getMap().getCameraPosition().target;
                onMapLocationChangedListener.onMapLocationChanged(target);
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
                onMapLocationChangedListener.onMapLocationChanged(latLng);
            }
        }

        LocationRequest request = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!useMapLocation) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
            onMapLocationChangedListener.onMapLocationChanged(latLng);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO Consider handling
    }

    interface OnMapLocationChangedListener {
        void onMapLocationChanged(LatLng latLng);
    }
}
