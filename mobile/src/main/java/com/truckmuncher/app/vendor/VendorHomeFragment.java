package com.truckmuncher.app.vendor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.truckmuncher.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;

public class VendorHomeFragment extends Fragment {

    private static final String ARG_CURRENT_LOCATION = "current_location";

    @InjectView(R.id.vendor_map_marker)
    ImageView vendorMapMarker;

    @InjectView(R.id.vendor_map_marker_pulse)
    ImageView vendorMarkerPulse;

    private Location currentLocation;
    private OnServingModeChangedListener onServingModeChangedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onServingModeChangedListener = (OnServingModeChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement " + OnServingModeChangedListener.class.getName());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vendor_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(ARG_CURRENT_LOCATION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_CURRENT_LOCATION, currentLocation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onServingModeChangedListener = null;
    }

    public void onLocationUpdate(Location location) {
        currentLocation = location;
    }

    @OnCheckedChanged(R.id.serving_mode)
    void onServingModeToggled(boolean isChecked) {
        int marker = isChecked ? R.drawable.map_marker_green : R.drawable.map_marker_gray;

        vendorMapMarker.setImageDrawable(getResources().getDrawable(marker));

        updateAnimation(isChecked);

        onServingModeChangedListener.onServingModeChanged(isChecked, currentLocation);
    }

    private void updateAnimation(boolean servingModeEnabled) {
        if (servingModeEnabled) {
            AnimationSet animations = new AnimationSet(false);
            animations.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.pulse));
            animations.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade));

            vendorMarkerPulse.startAnimation(animations);
            vendorMarkerPulse.setVisibility(View.VISIBLE);
        } else {
            vendorMarkerPulse.clearAnimation();
            vendorMarkerPulse.setVisibility(View.GONE);
        }
    }

    interface OnServingModeChangedListener {
        void onServingModeChanged(boolean enabled, Location currentLocation);
    }
}
