package com.truckmuncher.app.vendor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.facebook.Session;
import com.google.android.gms.maps.model.LatLng;
import com.truckmuncher.app.MainActivity;
import com.truckmuncher.app.R;
import com.truckmuncher.app.authentication.AccountGeneral;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;
import com.truckmuncher.app.vendor.menuadmin.MenuAdminFragment;
import com.truckmuncher.app.vendor.settings.VendorSettingsActivity;
import com.twitter.sdk.android.Twitter;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class VendorHomeActivity extends ActionBarActivity implements
        VendorMapFragment.OnMapLocationChangedListener, VendorHomeFragment.OnServingModeChangedListener,
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    private Spinner actionBarSpinner;

    private AccountManager accountManager;
    private String selectedTruckId;
    private VendorHomeServiceHelper serviceHelper;
    private ResetVendorTrucksServiceHelper resetServiceHelper;
    private SharedPreferences sharedPreferences;
    private SimpleCursorAdapter spinnerAdapter;
    private String[] truckIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setTitle("");

        actionBarSpinner = new Spinner(getSupportActionBar().getThemedContext());
        toolbar.addView(actionBarSpinner);

        getSupportLoaderManager().initLoader(0, savedInstanceState, this);

        serviceHelper = new VendorHomeServiceHelper();
        resetServiceHelper = new ResetVendorTrucksServiceHelper();
        accountManager = AccountManager.get(this);

        // Kick off a refresh of the vendor data
        startService(new Intent(this, VendorTrucksService.class));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vendor_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            doLogout();
            return true;
        } else if (item.getItemId() == R.id.action_menu) {
            showMenu();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, VendorSettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLogout() {
        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTH_TOKEN_TYPE);

            if (!TextUtils.isEmpty(authToken)) {
                accountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, authToken);
            }
        }

        Twitter.getSessionManager().clearActiveSession();
        Session facebookSession = Session.getActiveSession();

        if (facebookSession != null && facebookSession.isOpened()) {
            facebookSession.close();
        }

        resetServiceHelper.resetVendorTrucks(this, truckIds);

        exitVendorMode();
    }

    private void exitVendorMode() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapLocationChanged(LatLng latLng) {
        VendorHomeFragment fragment = (VendorHomeFragment) getSupportFragmentManager().findFragmentById(R.id.vendor_home_fragment);

        if (fragment != null) {
            Location location = new Location("");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            fragment.onLocationUpdate(location);
        }
    }

    @Override
    public void onServingModeChanged(final boolean enabled, Location currentLocation) {
        serviceHelper.changeServingState(this, selectedTruckId, enabled, currentLocation);
        actionBarSpinner.setEnabled(!enabled);

        final VendorMapFragment fragment = (VendorMapFragment) getSupportFragmentManager().findFragmentById(R.id.vendor_map_fragment);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.setMapControlsEnabled(!enabled);
            }
        });

        // If serving mode is being enabled and they have the item unavailable warning enabled,
        // we need to check if there are any items marked as unavailable
        if (enabled && sharedPreferences.getBoolean(getString(R.string.setting_item_unavailable_warning), true)) {
            // Get menu items that are marked as out of stock for the current truck
            WhereClause whereClause = new WhereClause.Builder()
                    .where(PublicContract.Menu.TRUCK_ID, EQUALS, selectedTruckId)
                    .and()
                    .where(PublicContract.Menu.IS_AVAILABLE, EQUALS, 0)
                    .build();
            String[] projection = ItemsOutOfStockQuery.PROJECTION;
            Uri uri = PublicContract.MENU_URI;

            Cursor cursor = getContentResolver().query(uri, projection,
                    whereClause.selection, whereClause.selectionArgs, null);

            // Show the warning if there are items out of stock
            if (cursor.getCount() > 0 ) {
                showWarning(cursor.getCount());
            }

            cursor.close();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.OWNED_BY_CURRENT_USER, EQUALS, 1)
                .build();
        String[] projection = TrucksOwnedByUserQuery.PROJECTION;
        Uri uri = PublicContract.TRUCK_URI;
        return new CursorLoader(this, uri, projection, whereClause.selection, whereClause.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        truckIds = new String[cursor.getCount()];

        if (cursor.moveToFirst()) {
            selectedTruckId = cursor.getString(TrucksOwnedByUserQuery.ID);

            int counter = 0;
            do {
                truckIds[counter] = cursor.getString(TrucksOwnedByUserQuery.ID);
                counter++;
            } while (cursor.moveToNext());
        }

        String[] adapterCols = {TrucksOwnedByUserQuery.PROJECTION[TrucksOwnedByUserQuery.NAME]};
        int[] adapterRowViews = new int[]{android.R.id.text1};
        spinnerAdapter = new SimpleCursorAdapter(getSupportActionBar().getThemedContext(),
                android.R.layout.simple_spinner_item, cursor, adapterCols, adapterRowViews, 0);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBarSpinner.setAdapter(spinnerAdapter);
        actionBarSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no-op
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedTruckId = truckIds[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // no-op
    }

    private void showMenu() {
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, MenuAdminFragment.newInstance(selectedTruckId), MenuAdminFragment.TAG)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void showWarning(int numItems) {
        View checkBoxView = View.inflate(this, R.layout.dialog_items_unavailable_warning, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox_dont_show_again);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (checkBox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.setting_item_unavailable_warning), false);
                    editor.apply();
                }

                if (id == AlertDialog.BUTTON_POSITIVE) {
                    showMenu();
                }
                dialog.cancel();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_items_unavailable));
        builder.setMessage(getResources().getQuantityString(R.plurals.items_unavailable_message, numItems, numItems))
                .setView(checkBoxView)
                .setPositiveButton(getString(R.string.items_unavailable_positive_button), listener)
                .setNegativeButton(getString(R.string.items_unavailable_negative_button), listener).show();
    }

    public interface TrucksOwnedByUserQuery {

        public static final String[] PROJECTION = new String[]{
                PublicContract.Truck._ID,
                PublicContract.Truck.ID,
                PublicContract.Truck.NAME
        };
        static final int _ID = 0;
        static final int ID = 1;
        static final int NAME = 2;
    }

    public interface ItemsOutOfStockQuery {

        public static final String[] PROJECTION = new String[]{
                PublicContract.MenuItem._ID
        };
    }
}
