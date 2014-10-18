package com.truckmuncher.truckmuncher.vendor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.google.android.gms.maps.model.LatLng;
import com.truckmuncher.truckmuncher.MainActivity;
import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.vendor.menuadmin.MenuAdminFragment;

public class VendorHomeActivity extends ActionBarActivity implements VendorMapFragment.OnMapLocationChangedListener {

    public static final String USERNAME = "VendorHomeActivity.username";

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        // Kick off a refresh of the vendor data
        startService(new Intent(this, VendorTrucksService.class));

        accountManager = AccountManager.get(this);

        Bundle extras = getIntent().getExtras();

        if (extras.getString(USERNAME) != null) {
            setTitle(extras.getString(USERNAME));
        }
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
            getFragmentManager().beginTransaction()
                    // FIXME Need to use a real truck id, not a mock one
                    .add(android.R.id.content, MenuAdminFragment.newInstance("Truck1"), MenuAdminFragment.TAG)
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
            String authToken = accountManager.peekAuthToken(accounts[0],
                    AccountGeneral.getAuthTokenType(this));

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
        VendorHomeFragment fragment = (VendorHomeFragment) getFragmentManager().findFragmentById(R.id.vendor_home_fragment);

        if (fragment != null) {
            Location location = new Location("");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            fragment.onLocationUpdate(location);
        }
    }
}
