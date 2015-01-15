package com.truckmuncher.truckmuncher.customer;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;
import com.truckmuncher.truckmuncher.data.sql.WhereClause;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.truckmuncher.truckmuncher.data.sql.WhereClause.Operator.EQUALS;

public class CustomerMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_TRUCK_ID = "truck_id";
    private static final int LOADER_HEADER = 0;
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

    public static CustomerMenuFragment newInstance(String truckId) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, truckId);
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
        adapter = new MenuAdapter(getActivity());
        setListAdapter(adapter);
        getListView().setFastScrollEnabled(true);
        getListView().setBackgroundColor(getResources().getColor(android.R.color.background_light));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_HEADER, getArguments(), this);
        getLoaderManager().initLoader(LOADER_MENU, getArguments(), this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String truckId = args.getString(ARG_TRUCK_ID);
        switch (id) {
            case LOADER_HEADER: {
                WhereClause whereClause = new WhereClause.Builder()
                        .where(PublicContract.Truck.ID, EQUALS, truckId)
                        .build();
                return new CursorLoader(getActivity(), PublicContract.TRUCK_URI, HeaderQuery.PROJECTION, whereClause.selection, whereClause.selectionArgs, null);
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
            case LOADER_HEADER:
                if (data.moveToFirst()) {
                    bindHeaderView(data);
                }
                break;
            case LOADER_MENU:
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
        String imageUrl = cursor.getString(HeaderQuery.IMAGE_URL);
        if (TextUtils.isEmpty(imageUrl)) {
            truckImage.setVisibility(View.GONE);
        } else {
            Picasso.with(getActivity()).load(imageUrl).into(truckImage);
        }

        String backgroundColor = cursor.getString(HeaderQuery.COLOR_PRIMARY);
        if (backgroundColor != null) {
            headerView.setBackgroundColor(Color.parseColor(backgroundColor));
            int textColor = ColorCorrector.calculateTextColor(backgroundColor);
            truckName.setTextColor(textColor);
            truckKeywords.setTextColor(textColor);
        }

        truckName.setText(cursor.getString(HeaderQuery.NAME));
        truckKeywords.setText(cursor.getString(HeaderQuery.KEYWORDS));
    }

    interface HeaderQuery {
        static final String[] PROJECTION = new String[]{
                PublicContract.Truck.NAME,
                PublicContract.Truck.IMAGE_URL,
                PublicContract.Truck.KEYWORDS,
                PublicContract.Truck.COLOR_PRIMARY
        };
        static final int NAME = 0;
        static final int IMAGE_URL = 1;
        static final int KEYWORDS = 2;
        static final int COLOR_PRIMARY = 3;
    }
}
