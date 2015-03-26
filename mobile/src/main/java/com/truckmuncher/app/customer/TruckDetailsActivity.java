package com.truckmuncher.app.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.truckmuncher.app.R;

import java.net.URI;
import java.net.URISyntaxException;

import static com.guava.common.base.Preconditions.checkNotNull;

public class TruckDetailsActivity extends ActionBarActivity implements TruckDataLoaderHandler.OnTriedToLoadInvalidTruckListener {

    private static final String ARG_TRUCK_ID = "truck_id";

    public static Intent newIntent(Context context, @NonNull String truckId) {
        Intent intent = new Intent(context, TruckDetailsActivity.class);
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
            try {

                // We expect: truckmuncher.com/#/trucks/truck-id
                URI javaUri = new URI(intent.getData().toString());

                // if the uri contains a # symbol, the data will be in the fragment; otherwise in the path
                String key = javaUri.getFragment() != null ? javaUri.getFragment() : javaUri.getPath();
                String[] segments = key.substring(1).split("/");
                int size = segments.length;

                if (size < 2 || !segments[size - 2].equalsIgnoreCase("trucks")) {
                    throw new URISyntaxException("", "Can't handle this uri");
                }
                truckId = segments[size - 1];
            } catch (URISyntaxException e) {
                Intent forwardingIntent = new Intent(Intent.ACTION_VIEW);
                forwardingIntent.setData(intent.getData());
                startActivity(forwardingIntent);
                finish();
                return;
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, CustomerMenuFragment.newInstance(truckId))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, parent)) {
                    NavUtils.navigateUpTo(this, parent);
                } else {
                    TaskStackBuilder.create(this)
                            .addParentStack(this)
                            .startActivities();
                }
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
