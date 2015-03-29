package com.truckmuncher.app.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.truckmuncher.app.R;

import static com.guava.common.base.Preconditions.checkNotNull;

public class TruckProfileActivity extends ActionBarActivity implements CustomerMenuFragment.OnTriedToLoadInvalidTruckListener {

    private static final String ARG_TRUCK_ID = "truck_id";

    public static Intent newIntent(Context context, @NonNull String truckId) {
        Intent intent = new Intent(context, TruckProfileActivity.class);
        intent.putExtra(ARG_TRUCK_ID, checkNotNull(truckId));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String truckId;
        Intent intent = getIntent();
        if (intent.getData() == null) {

            // We were launched directly
            truckId = intent.getStringExtra(ARG_TRUCK_ID);
        } else {

            // Launched through deep link
            truckId = intent.getData().getLastPathSegment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, CustomerMenuFragment.newInstance(truckId, null))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTriedToLoadInvalidTruck() {
        Toast.makeText(this, R.string.error_invalid_truck, Toast.LENGTH_LONG).show();
        finish();
    }
}
