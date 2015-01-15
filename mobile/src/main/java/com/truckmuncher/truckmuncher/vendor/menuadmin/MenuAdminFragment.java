package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;
import com.truckmuncher.truckmuncher.data.sql.WhereClause;

import java.util.Map;

import static com.truckmuncher.truckmuncher.data.sql.WhereClause.Operator.EQUALS;

public class MenuAdminFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MenuAdminFragment.class.getSimpleName();
    private static final String ARG_TRUCK_ID = "truck_id";

    private MenuAdminAdapter adapter;
    private android.view.MenuItem actionMenu;
    private MenuAdminServiceHelper serviceHelper;

    public static MenuAdminFragment newInstance(String truckId) {
        Bundle args = new Bundle();
        args.putString(ARG_TRUCK_ID, truckId);
        MenuAdminFragment fragment = new MenuAdminFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        setEmptyText(getString(R.string.error_empty_vendor_menu));
        serviceHelper = new MenuAdminServiceHelper();
        adapter = new MenuAdminAdapter(getActivity(), null);
        getListView().setFastScrollEnabled(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Map<String, Boolean> diff = adapter.getMenuItemAvailabilityDiff();
        adapter.clearMenuItemAvailabilityDiff();
        serviceHelper.persistMenuDiff(getActivity(), diff);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        actionMenu = menu.findItem(R.id.action_menu);
        actionMenu.setVisible(false);
    }

    @Override
    public void onDestroyOptionsMenu() {
        actionMenu.setVisible(true);
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
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
