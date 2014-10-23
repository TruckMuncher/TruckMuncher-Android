package com.truckmuncher.truckmuncher.vendor;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.SimpleAsyncQueryHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;

public class VendorHomeFragment extends Fragment {

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnCheckedChanged(R.id.serving_mode)
    void onServingModeToggled(CompoundButton servingModeSwitch, boolean isChecked) {
        int marker = isChecked ? R.drawable.map_marker_green : R.drawable.map_marker_gray;

        vendorMapMarker.setImageDrawable(getResources().getDrawable(marker));

        updateAnimation(isChecked);

        ContentValues values = new ContentValues();
        values.put(Contract.TruckEntry.COLUMN_LATITUDE, currentLocation.getLatitude());
        values.put(Contract.TruckEntry.COLUMN_LONGITUDE, currentLocation.getLongitude());
        values.put(Contract.TruckEntry.COLUMN_IS_SERVING, isChecked);
        values.put(Contract.TruckEntry.COLUMN_IS_DIRTY, true);
        AsyncQueryHandler handler = new SimpleAsyncQueryHandler(getActivity().getContentResolver());
        // FIXME Need to use a real truck id, not a mock one
        handler.startUpdate(0, null, Contract.buildNeedsSync(Contract.TruckEntry.buildSingleTruck("Truck1")), values, null, null);

        onServingModeChangedListener.onServingModeChanged(isChecked);
    }

    public void onLocationUpdate(Location location) {
        currentLocation = location;
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
        void onServingModeChanged(boolean enabled);
    }
}
