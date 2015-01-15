package com.truckmuncher.truckmuncher.vendor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.google.android.gms.maps.model.LatLng;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.truckmuncher.MainActivity;
import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;
import com.truckmuncher.truckmuncher.data.sql.WhereClause;
import com.truckmuncher.truckmuncher.vendor.menuadmin.MenuAdminFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.truckmuncher.truckmuncher.data.sql.WhereClause.Operator.EQUALS;

public class VendorHomeActivity extends ActionBarActivity implements
        VendorMapFragment.OnMapLocationChangedListener, VendorHomeFragment.OnServingModeChangedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private AccountManager accountManager;
    private List<Truck> trucksOwnedByUser = Collections.emptyList();
    private Truck selectedTruck;
    private VendorHomeServiceHelper serviceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        getSupportLoaderManager().initLoader(0, savedInstanceState, this);

        serviceHelper = new VendorHomeServiceHelper();
        accountManager = AccountManager.get(this);

        // Kick off a refresh of the vendor data
        startService(new Intent(this, VendorTrucksService.class));
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
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, MenuAdminFragment.newInstance(selectedTruck.id), MenuAdminFragment.TAG)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLogout() {
        SocialNetworkManager.getInstance(this).logout();

        Account[] accounts = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], AccountGeneral.AUTH_TOKEN_TYPE);

            if (!TextUtils.isEmpty(authToken)) {
                accountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, authToken);
            }
        }

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
        serviceHelper.changeServingState(this, selectedTruck.id, enabled, currentLocation);

        final VendorMapFragment fragment = (VendorMapFragment) getSupportFragmentManager().findFragmentById(R.id.vendor_map_fragment);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.setMapControlsEnabled(!enabled);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.OWNED_BY_CURRENT_USER, EQUALS, 1)
                .build();
        String[] projection = TrucksOwnedByUserQuery.PROJECTION;
        Uri uri = Contract.TRUCK_PROPERTIES_URI;
        return new CursorLoader(this, uri, projection, whereClause.selection, whereClause.selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToPosition(-1);

        trucksOwnedByUser = new ArrayList<>();

        while(cursor.moveToNext()) {
            Truck truck = new Truck.Builder()
                    .id(cursor.getString(TrucksOwnedByUserQuery.ID))
                    .name(cursor.getString(TrucksOwnedByUserQuery.NAME))
                    .build();

            trucksOwnedByUser.add(truck);
        }

        if (trucksOwnedByUser.size() > 0) {
            selectedTruck = trucksOwnedByUser.get(0);
            setTitle(selectedTruck.name);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no-op
    }

    public interface TrucksOwnedByUserQuery {

        public static final String[] PROJECTION = new String[]{
                PublicContract.Truck.ID,
                PublicContract.Truck.NAME
        };
        static final int ID = 0;
        static final int NAME = 1;
    }
}
