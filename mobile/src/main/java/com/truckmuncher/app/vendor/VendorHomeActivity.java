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
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.facebook.Session;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.app.MainActivity;
import com.truckmuncher.app.R;
import com.truckmuncher.app.authentication.AccountGeneral;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;
import com.truckmuncher.app.vendor.menuadmin.MenuAdminFragment;
import com.truckmuncher.app.vendor.settings.VendorSettingsActivity;
import com.twitter.sdk.android.Twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class VendorHomeActivity extends ActionBarActivity implements
        VendorHomeFragment.OnServingModeChangedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private AccountManager accountManager;
    private List<Truck> trucksOwnedByUser = Collections.emptyList();
    private Truck selectedTruck;
    private VendorHomeServiceHelper serviceHelper;
    private ResetVendorTrucksServiceHelper resetServiceHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

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

        resetServiceHelper.resetVendorTrucks(this, trucksOwnedByUser);

        exitVendorMode();
    }

    private void exitVendorMode() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onServingModeChanged(final boolean enabled, Location currentLocation) {
        serviceHelper.changeServingState(this, selectedTruck.id, enabled, currentLocation);

        // If serving mode is being enabled and they have the item unavailable warning enabled,
        // we need to check if there are any items marked as unavailable
        if (enabled && sharedPreferences.getBoolean(getString(R.string.setting_item_unavailable_warning), true)) {
            // Get menu items that are marked as out of stock for the current truck
            WhereClause whereClause = new WhereClause.Builder()
                    .where(PublicContract.Menu.TRUCK_ID, EQUALS, selectedTruck.id)
                    .and()
                    .where(PublicContract.Menu.IS_AVAILABLE, EQUALS, 0)
                    .build();
            String[] projection = ItemsOutOfStockQuery.PROJECTION;
            Uri uri = PublicContract.MENU_URI;

            Cursor cursor = getContentResolver().query(uri, projection,
                    whereClause.selection, whereClause.selectionArgs, null);

            // Show the warning if there are items out of stock
            if (cursor.getCount() > 0) {
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
        cursor.moveToPosition(-1);

        trucksOwnedByUser = new ArrayList<>();

        while (cursor.moveToNext()) {
            Truck truck = new Truck.Builder()
                    .id(cursor.getString(TrucksOwnedByUserQuery.ID))
                    .name(cursor.getString(TrucksOwnedByUserQuery.NAME))
                    .imageUrl(cursor.getString(TrucksOwnedByUserQuery.IMAGE_URL))
                    .keywords(Contract.convertStringToList(cursor.getString(TrucksOwnedByUserQuery.KEYWORDS)))
                    .primaryColor(cursor.getString(TrucksOwnedByUserQuery.COLOR_PRIMARY))
                    .secondaryColor(cursor.getString(TrucksOwnedByUserQuery.COLOR_SECONDARY))
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

    private void showMenu() {
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, MenuAdminFragment.newInstance(selectedTruck.id), MenuAdminFragment.TAG)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void showWarning(int numItems) {
        View checkBoxView = View.inflate(this, R.layout.dialog_items_unavailable_warning, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox_dont_show_again);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(@NonNull DialogInterface dialog, int id) {
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
                PublicContract.Truck.ID,
                PublicContract.Truck.NAME,
                PublicContract.Truck.IMAGE_URL,
                PublicContract.Truck.KEYWORDS,
                PublicContract.Truck.COLOR_PRIMARY,
                PublicContract.Truck.COLOR_SECONDARY
        };
        static final int ID = 0;
        static final int NAME = 1;
        static final int IMAGE_URL = 2;
        static final int KEYWORDS = 3;
        static final int COLOR_PRIMARY = 4;
        static final int COLOR_SECONDARY = 5;
    }

    public interface ItemsOutOfStockQuery {

        public static final String[] PROJECTION = new String[]{
                PublicContract.MenuItem._ID
        };
    }
}
