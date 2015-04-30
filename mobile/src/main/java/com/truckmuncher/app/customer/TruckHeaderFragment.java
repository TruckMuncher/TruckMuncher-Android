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
import com.truckmuncher.app.App;
import com.truckmuncher.app.R;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

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

    @Inject
    UserAccount userAccount;

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
    @InjectView(R.id.favorited)
    ImageView favoritedView;
    @InjectView(R.id.not_favorited)
    ImageView notFavoritedView;

    private OnTruckHeaderClickListener truckHeaderClickListener;
    private LatLng referenceLocation;

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
        App.get(getActivity()).inject(this);

        referenceLocation = getArguments().getParcelable(ARG_LOCATION);
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

        return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, TruckQuery.PROJECTION,
                whereClause.selection, whereClause.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {

            // Split the keywords and format them in a way that is user friendly
            String keywordsString = cursor.getString(TruckQuery.KEYWORDS);
            List<String> keywords = PublicContract.convertStringToList(keywordsString);
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
            String primaryColor = cursor.getString(TruckQuery.COLOR_PRIMARY);
            Double latitude = cursor.getDouble(TruckQuery.LATITUDE);
            Double longitude = cursor.getDouble(TruckQuery.LONGITUDE);
            Boolean isFavorite = cursor.getInt(TruckQuery.IS_FAVORITE) == 1;
            LatLng truckLocation = new LatLng(latitude, longitude);
            onTruckDataLoaded(truckName, truckKeywords, imageUrl, primaryColor, truckLocation, isFavorite);
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
        if (truckHeaderClickListener != null) {
            truckHeaderClickListener.onTruckHeaderClick(getArguments().getString(ARG_TRUCK_ID));
        }
    }

    @OnClick(R.id.favorited)
    void onFavoriteClick() {
        getActivity().startService(RemoveFavoriteTruckService.newIntent(getActivity(), getArguments().getString(ARG_TRUCK_ID)));

        favoritedView.setVisibility(View.GONE);
        notFavoritedView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.not_favorited)
    void onUnfavoriteClick() {
        getActivity().startService(AddFavoriteTruckService.newIntent(getActivity(), getArguments().getString(ARG_TRUCK_ID)));

        favoritedView.setVisibility(View.VISIBLE);
        notFavoritedView.setVisibility(View.GONE);
    }

    public void setOnTruckHeaderClickListener(OnTruckHeaderClickListener listener) {
        truckHeaderClickListener = listener;
    }

    private void onTruckDataLoaded(String name, String keywords, String imageUrl, String headerColor,
                                   LatLng truckLocation, boolean isFavorite) {
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

        // User is logged in. Show/allow favoriting of trucks
        if (!TextUtils.isEmpty(userAccount.getAuthToken())) {
            if (isFavorite) {
                favoritedView.setVisibility(View.VISIBLE);
                notFavoritedView.setVisibility(View.GONE);
            } else {
                favoritedView.setVisibility(View.GONE);
                notFavoritedView.setVisibility(View.VISIBLE);
            }
        } else {
            favoritedView.setVisibility(View.GONE);
            notFavoritedView.setVisibility(View.GONE);
        }

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
                PublicContract.Truck.COLOR_PRIMARY,
                PublicContract.Truck.LATITUDE,
                PublicContract.Truck.LONGITUDE,
                PublicContract.Truck.IS_FAVORITE
        };
        int NAME = 0;
        int IMAGE_URL = 1;
        int KEYWORDS = 2;
        int COLOR_PRIMARY = 3;
        int LATITUDE = 4;
        int LONGITUDE = 5;
        int IS_FAVORITE = 6;
    }
}
