package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.truckmuncher.app.R;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

import static com.guava.common.base.Preconditions.checkNotNull;
import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class CustomerMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_TRUCK_ID = "truck_id";
    private static final int LOADER_TRUCK = 0;
    private static final int LOADER_MENU = 1;
    @InjectView(R.id.truck_name)
    TextView truckName;
    @InjectView(R.id.truck_keywords)
    TextView truckKeywords;
    @InjectView(R.id.truck_image)
    ImageView truckImage;
    @InjectView(R.id.header)
    View headerView;
    private MenuAdapter adapter;
    private String truckPrimaryColor;

    public static CustomerMenuFragment newInstance(@NonNull String truckId) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, checkNotNull(truckId));
        CustomerMenuFragment fragment = new CustomerMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_menu, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        getListView().setFastScrollEnabled(true);
        getListView().setBackgroundColor(getResources().getColor(android.R.color.background_light));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_TRUCK, getArguments(), this);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String truckId = args.getString(ARG_TRUCK_ID);
        switch (id) {
            case LOADER_TRUCK: {
                WhereClause whereClause = new WhereClause.Builder()
                        .where(PublicContract.Truck.ID, EQUALS, truckId)
                        .build();
                return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, TruckQuery.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
            }
            case LOADER_MENU: {
                WhereClause whereClause = new WhereClause.Builder()
                        .where(PublicContract.Menu.TRUCK_ID, EQUALS, truckId)
                        .build();
                String[] projection = MenuAdapter.Query.PROJECTION;
                Uri uri = Contract.syncFromNetwork(PublicContract.MENU_URI);
                return new CursorLoader(getActivity(), uri, projection, whereClause.selection, whereClause.selectionArgs, null);
            }
            default:
                throw new IllegalArgumentException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_TRUCK:
                if (data.moveToFirst()) {

                    // Wait to load the menu until we have a truck so that we for sure have the category color
                    truckPrimaryColor = data.getString(TruckQuery.COLOR_PRIMARY);
                    getLoaderManager().initLoader(LOADER_MENU, getArguments(), this);
                    bindHeaderView(data);

                    if (truckPrimaryColor != null) {
                        getListView().setBackgroundColor(Color.parseColor(truckPrimaryColor));
                    }
                } else {

                    // Invalid truck
                    Timber.w("Tried to load an invalid truck with id %s", getArguments().getString(ARG_TRUCK_ID));
                    ((OnTriedToLoadInvalidTruckListener) getActivity()).onTriedToLoadInvalidTruck();
                }
                break;
            case LOADER_MENU:
                if (adapter == null) {
                    int textColor;
                    if (truckPrimaryColor != null) {
                        textColor = ColorCorrector.calculateTextColor(truckPrimaryColor);
                    } else {
                        textColor = Color.BLACK;
                    }
                    adapter = new MenuAdapter(getActivity(), textColor);
                    setListAdapter(adapter);
                }
                adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_MENU:
                adapter.swapCursor(null);
                break;
        }
    }

    public View getHeaderView() {
        return headerView;
    }

    private void bindHeaderView(Cursor cursor) {
        String imageUrl = cursor.getString(TruckQuery.IMAGE_URL);
        if (TextUtils.isEmpty(imageUrl)) {
            truckImage.setVisibility(View.GONE);
        } else {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .fit()
                    .centerInside()
                    .into(truckImage);
        }

        String backgroundColor = cursor.getString(TruckQuery.COLOR_SECONDARY);
        if (backgroundColor != null) {
            headerView.setBackgroundColor(Color.parseColor(backgroundColor));
            int textColor = ColorCorrector.calculateTextColor(backgroundColor);
            truckName.setTextColor(textColor);
            truckKeywords.setTextColor(textColor);
        }

        truckName.setText(cursor.getString(TruckQuery.NAME));
        truckKeywords.setText(cursor.getString(TruckQuery.KEYWORDS));
        truckName.setText(cursor.getString(TruckQuery.NAME));

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
        truckKeywords.setText(builder.toString());
    }

    interface TruckQuery {
        static final String[] PROJECTION = new String[]{
                PublicContract.Truck.NAME,
                PublicContract.Truck.IMAGE_URL,
                PublicContract.Truck.KEYWORDS,
                PublicContract.Truck.COLOR_PRIMARY,
                PublicContract.Truck.COLOR_SECONDARY
        };
        static final int NAME = 0;
        static final int IMAGE_URL = 1;
        static final int KEYWORDS = 2;
        static final int COLOR_PRIMARY = 3;
        static final int COLOR_SECONDARY = 4;
    }

    public interface OnTriedToLoadInvalidTruckListener {
        void onTriedToLoadInvalidTruck();
    }
}
