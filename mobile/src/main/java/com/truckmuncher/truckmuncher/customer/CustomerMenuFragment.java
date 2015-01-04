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
import com.truckmuncher.truckmuncher.vendor.menuadmin.MenuAdminAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.truckmuncher.truckmuncher.data.sql.WhereClause.Operator.EQUALS;

public class CustomerMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_TRUCK_ID = "truck_id";
    private static final String ARG_TRUCK_NAME = "truck_name";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_KEYWORDS = "keywords";
    private static final String ARG_COLOR_PRIMARY = "color_primary";
    @InjectView(R.id.truck_name)
    TextView truckName;
    @InjectView(R.id.truck_keywords)
    TextView truckKeywords;
    @InjectView(R.id.truck_image)
    ImageView truckImage;
    @InjectView(R.id.header)
    View headerView;
    private MenuAdminAdapter adapter;

    public static CustomerMenuFragment newInstance(String truckId, String truckName, String imageUrl, String keywords, String primaryColor) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, truckId);
        args.putString(ARG_TRUCK_NAME, truckName);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_KEYWORDS, keywords);
        args.putString(ARG_COLOR_PRIMARY, primaryColor);
        CustomerMenuFragment fragment = new CustomerMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_menu, container, false);
        ButterKnife.inject(this, view);
        Bundle args = getArguments();

        Picasso.with(getActivity()).load(args.getString(ARG_IMAGE_URL)).into(truckImage);

        truckName.setText(args.getString(ARG_TRUCK_ID));
        truckKeywords.setText(args.getString(ARG_KEYWORDS));
        String color = args.getString(ARG_COLOR_PRIMARY);
        if (color != null) {
            view.findViewById(R.id.header).setBackgroundColor(Color.parseColor(color));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new MenuAdminAdapter(getActivity(), null);
        setListAdapter(adapter);
        getListView().setFastScrollEnabled(true);
        getListView().setBackgroundColor(getResources().getColor(android.R.color.background_light));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String truckId = args.getString(ARG_TRUCK_ID);
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Menu.TRUCK_ID, EQUALS, truckId)
                .build();
        String[] projection = MenuAdminAdapter.Query.PROJECTION;
        Uri uri = Contract.syncFromNetwork(PublicContract.MENU_URI);
        return new CursorLoader(getActivity(), uri, projection, whereClause.selection, whereClause.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public View getHeaderView() {
        return headerView;
    }
}
