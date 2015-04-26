package com.truckmuncher.app.vendor;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.github.mrengineer13.snackbar.SnackBar;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.truckmuncher.app.App;
import com.truckmuncher.app.MainActivity;
import com.truckmuncher.app.R;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.common.RateUs;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;
import com.truckmuncher.app.vendor.menuadmin.MenuAdminFragment;
import com.truckmuncher.app.vendor.settings.VendorSettingsActivity;

import javax.inject.Inject;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class VendorHomeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        VendorHomeFragment.OnServingModeChangedListener,
        VendorHomeController.VendorHomeUi {

    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    UserAccount account;
    @Inject
    Bus bus;
    @Inject
    VendorHomeController controller;

    private Spinner actionBarSpinner;
    private VendorHomeServiceHelper serviceHelper;
    private SimpleCursorAdapter spinnerAdapter;

    public static Intent newIntent(Context context) {
        return new Intent(context, VendorHomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        actionBarSpinner = new Spinner(getSupportActionBar().getThemedContext());
        toolbar.addView(actionBarSpinner);
        actionBarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) spinnerAdapter.getItem(position);
                String truckId = cursor.getString(TrucksOwnedByUserQuery.ID);
                controller.setSelectedTruckId(truckId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        App.get(this).inject(this);
        controller.setVendorHomeUi(this);

        getSupportLoaderManager().initLoader(0, null, this);

        serviceHelper = new VendorHomeServiceHelper();

        // Kick off a refresh of the vendor data
        startService(VendorTrucksService.newIntent(this));

        RateUs.check(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vendor_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            doLogout();
            return true;
        } else if (item.getItemId() == R.id.action_menu) {
            controller.onEditMenuClicked();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            startActivity(VendorSettingsActivity.newIntent(this));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exitVendorMode() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onServingModeChanged(final boolean enabled, Location currentLocation) {
        String selectedTruckId = controller.getSelectedTruckId();
        serviceHelper.changeServingState(this, selectedTruckId, enabled, currentLocation);
        actionBarSpinner.setEnabled(!enabled);

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
            if (cursor.getCount() > 0) {
                showWarning(cursor.getCount());
            }

            cursor.close();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        WhereClause whereClause = new WhereClause.Builder()
                .where(PublicContract.Truck.OWNER_ID, EQUALS, account.getUserId())
                .build();
        String[] projection = TrucksOwnedByUserQuery.PROJECTION;
        Uri uri = PublicContract.TRUCK_URI;
        return new CursorLoader(this, uri, projection, whereClause.selection, whereClause.selectionArgs, "upper(" + PublicContract.Truck.NAME + ")");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        String[] adapterCols = {TrucksOwnedByUserQuery.PROJECTION[TrucksOwnedByUserQuery.NAME]};
        int[] adapterRowViews = new int[]{android.R.id.text1};
        spinnerAdapter = new SimpleCursorAdapter(getSupportActionBar().getThemedContext(),
                android.R.layout.simple_spinner_item, cursor, adapterCols, adapterRowViews, 0);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBarSpinner.setAdapter(spinnerAdapter);
        if (spinnerAdapter.getCount() > 0) {
            actionBarSpinner.setSelection(0, true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no-op
    }

    @Override
    public void showNoTrucksError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SnackBar.Builder(VendorHomeActivity.this)
                        .withMessageId(R.string.error_no_vendor_trucks)
                        .withActionMessageId(R.string.action_add_truck)
                        .withStyle(SnackBar.Style.INFO)
                        .withOnClickListener(new SnackBar.OnMessageClickListener() {
                            @Override
                            public void onMessageClick(Parcelable parcelable) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://www.truckmuncher.com/#/login"));
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void showEditMenuUi(String truckId) {
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, MenuAdminFragment.newInstance(truckId), MenuAdminFragment.TAG)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Subscribe
    public void onSyncCompleted(VendorTruckStateResolver.CompletedEvent event) {
        getSupportLoaderManager().restartLoader(0, null, this);
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
                    showEditMenuUi(controller.getSelectedTruckId());
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

    private void doLogout() {
        account.logout();
        exitVendorMode();
    }

    public interface TrucksOwnedByUserQuery {

        String[] PROJECTION = new String[]{
                PublicContract.Truck._ID,
                PublicContract.Truck.ID,
                PublicContract.Truck.NAME
        };
        int _ID = 0;
        int ID = 1;
        int NAME = 2;
    }

    public interface ItemsOutOfStockQuery {

        String[] PROJECTION = new String[]{
                PublicContract.MenuItem._ID
        };
    }
}
