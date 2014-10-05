package com.truckmuncher.truckmuncher.vendor;

import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.truckmuncher.truckmuncher.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class VendorHomeFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener, LocationListener {

    @InjectView(R.id.serving_mode)
    Switch servingModeSwitch;

    @InjectView(R.id.vendor_location)
    TextView vendorLocationTextView;

    GoogleMap map;

    private LocationManager locationManager;
    private Criteria locationProviderCriteria;
    private Location currentLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vendor_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.vendor_map)).getMap();
        ButterKnife.inject(this, view);
        servingModeSwitch.setOnCheckedChangeListener(this);

        setUpLocationManager();
        updateLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        String provider = locationManager.getBestProvider(locationProviderCriteria, true);
        locationManager.requestLocationUpdates(provider, 2000, 10, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        int color = checked ? R.color.serving_mode_on : R.color.serving_mode_off;

        servingModeSwitch.setBackgroundResource(color);
        vendorLocationTextView.setBackgroundResource(color);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void setUpLocationManager() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationProviderCriteria = new Criteria();
        locationProviderCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationProviderCriteria.setPowerRequirement(Criteria.POWER_LOW);
        locationProviderCriteria.setAltitudeRequired(false);
        locationProviderCriteria.setBearingRequired(false);
        locationProviderCriteria.setSpeedRequired(false);
        locationProviderCriteria.setCostAllowed(true);
    }

    private void updateLocation() {
        String provider = locationManager.getBestProvider(locationProviderCriteria, true);

        currentLocation = locationManager.getLastKnownLocation(provider);
        LatLng center = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(center)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(center);
        map.moveCamera(cameraUpdate);
        map.moveCamera(CameraUpdateFactory.zoomTo(14));

        vendorLocationTextView.setText(getLocationAddress(currentLocation));
    }

    private String getLocationAddress(Location location) {
        String addressString = "Address information unavailable";

        return addressString;
    }
}
