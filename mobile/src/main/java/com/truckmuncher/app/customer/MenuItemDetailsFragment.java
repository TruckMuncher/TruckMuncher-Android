package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.truckmuncher.app.R;

public class MenuItemDetailsFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_ITEM_URI = "arg_item_uri";

    public static MenuItemDetailsFragment newInstance(Uri itemUri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM_URI, itemUri);
        MenuItemDetailsFragment fragment = new MenuItemDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

//        return Cur;
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // No-op
    }
}
