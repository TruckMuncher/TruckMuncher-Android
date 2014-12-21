package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.sql.Query;

import java.util.Map;

public class MenuAdminFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MenuAdminFragment.class.getSimpleName();
    public static final String ARG_TRUCK_ID = "truck_id";

    private MenuAdminAdapter adapter;
    private MenuItem actionMenu;

    public MenuAdminFragment() {
    }

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
    public void onPause() {
        super.onPause();
        Map<String, Boolean> diff = adapter.getMenuItemAvailabilityDiff();
        adapter.clearMenuItemAvailabilityDiff();
        ContentValues[] contentValues = new ContentValues[diff.size()];
        int i = 0;
        for (Map.Entry<String, Boolean> entry : diff.entrySet()) {
            ContentValues values = new ContentValues(2);
            values.put(Contract.MenuItemEntry.COLUMN_INTERNAL_ID, entry.getKey());
            values.put(Contract.MenuItemEntry.COLUMN_IS_AVAILABLE, entry.getValue());
            contentValues[i] = values;
            i++;
        }
        Intent intent = new Intent(getActivity(), InsertMenuItemDiffService.class);
        intent.putExtra(InsertMenuItemDiffService.ARG_VALUES, contentValues);
        getActivity().startService(intent);
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
        Query query = Contract.MenuEntry.buildMenuForTruck(truckId);
        String[] projection = MenuAdminAdapter.Query.PROJECTION;
        Uri uri = Contract.syncFromNetwork(Contract.MenuEntry.CONTENT_URI);
        return new CursorLoader(getActivity(), uri, projection, query.selection, query.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
