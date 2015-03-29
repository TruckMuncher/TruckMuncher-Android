package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.truckmuncher.app.R;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.guava.common.base.Preconditions.checkNotNull;
import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class TruckHeaderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final double METERS_TO_MILES = 0.000621371;
    private static final String ARG_TRUCK_ID = "truck_id";
    private static final String ARG_LOCATION = "location";
    @InjectView(R.id.truck_name)
    TextView truckName;
    @InjectView(R.id.truck_keywords)
    TextView truckKeywords;
    @InjectView(R.id.distance_from_location)
    TextView distanceFromLocation;
    @InjectView(R.id.truck_image)
    ImageView truckImage;
    @InjectView(R.id.header)
    View headerView;

    private OnTruckHeaderClickListener truckHeaderClickListener;

    // TODO use a dynamic user location instead of a static one
    public static TruckHeaderFragment newInstance(@NonNull String truckId, LatLng userLocation) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, checkNotNull(truckId));
        args.putParcelable(ARG_LOCATION, userLocation);
        TruckHeaderFragment fragment = new TruckHeaderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truck_header, container, false);
        ButterKnife.inject(this, view);
        truckHeaderClickListener = (OnTruckHeaderClickListener) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String truckId = getArguments().getString(ARG_TRUCK_ID);
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.ID, EQUALS, truckId)
                .build();
        return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, TruckQuery.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {

            // Split the keywords and format them in a way that is user friendly
            String keywordsString = cursor.getString(TruckQuery.KEYWORDS);
            List<String> keywords = Contract.convertStringToList(keywordsString);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < keywords.size(); i++) {
                builder.append(keywords.get(i));
                if (i < keywords.size() - 1) {
                    builder.append(", ");
                }
            }

            String truckName = cursor.getString(TruckQuery.NAME);
            String truckKeywords = builder.toString();
            String imageUrl = cursor.getString(TruckQuery.IMAGE_URL);
            String secondaryColor = cursor.getString(TruckQuery.COLOR_SECONDARY);
            LatLng truckLocation = new LatLng(cursor.getDouble(TruckQuery.LATITUDE), cursor.getDouble(TruckQuery.LONGITUDE));
            onTruckDataLoaded(truckName, truckKeywords, imageUrl, secondaryColor, truckLocation);
        } else {

            // Invalid truck
            String truckId = getArguments().getString(ARG_TRUCK_ID);
            Timber.w(new IllegalStateException("Tried to load an invalid truck with id " + truckId), "");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // No-op
    }

    @OnClick(R.id.header)
    void onHeaderClick() {
        truckHeaderClickListener.onTruckHeaderClick(getArguments().getString(ARG_TRUCK_ID));
    }

    private void onTruckDataLoaded(String name, String keywords, String imageUrl, String headerColor, LatLng truckLocation) {
        if (TextUtils.isEmpty(imageUrl)) {
            truckImage.setVisibility(View.GONE);
        } else {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .fit()
                    .centerInside()
                    .transform(new CircleTransform())
                    .into(truckImage);
        }

        if (headerColor != null) {
            headerView.setBackgroundColor(Color.parseColor(headerColor));
            int textColor = ColorCorrector.calculateTextColor(headerColor);
            truckName.setTextColor(textColor);
            truckKeywords.setTextColor(textColor);
        }

        truckName.setText(name);
        truckKeywords.setText(keywords);

        LatLng referenceLocation = getArguments().getParcelable(ARG_LOCATION);
        if (referenceLocation != null) {
            // distance in meters
            double delta = SphericalUtil.computeDistanceBetween(truckLocation, referenceLocation);
            // convert to miles
            delta *= METERS_TO_MILES;

            distanceFromLocation.setText(new DecimalFormat("0.0").format(delta) + " mi");
            distanceFromLocation.setVisibility(View.VISIBLE);
        } else {
            distanceFromLocation.setVisibility(View.GONE);
        }
    }

    public interface OnTruckHeaderClickListener {
        void onTruckHeaderClick(String currentTruck);
    }

    interface TruckQuery {
        String[] PROJECTION = new String[]{
                PublicContract.Truck.NAME,
                PublicContract.Truck.IMAGE_URL,
                PublicContract.Truck.KEYWORDS,
                PublicContract.Truck.COLOR_SECONDARY,
                PublicContract.Truck.LATITUDE,
                PublicContract.Truck.LONGITUDE
        };
        int NAME = 0;
        int IMAGE_URL = 1;
        int KEYWORDS = 2;
        int COLOR_SECONDARY = 3;
        int LATITUDE = 4;
        int LONGITUDE = 5;
    }
}
