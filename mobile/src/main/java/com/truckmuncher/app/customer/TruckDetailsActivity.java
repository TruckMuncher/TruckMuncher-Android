package com.truckmuncher.app.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.truckmuncher.app.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.guava.common.base.Preconditions.checkNotNull;

public class TruckDetailsActivity extends AppCompatActivity implements TruckDataLoaderHandler.OnTriedToLoadInvalidTruckListener {

    public static final String ARG_ENDING_TRUCK = "ending_truck";
    private static final String ARG_TRUCK_IDS = "truck_ids";
    private static final String ARG_STARTING_TRUCK = "starting_truck";

    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    TruckDetailsPagerAdapter adapter;

    /**
     * @param startingTruck the first truck to display
     */
    public static Intent newIntent(Context context, @NonNull ArrayList<String> truckIds, @NonNull String startingTruck) {
        Intent intent = new Intent(context, TruckDetailsActivity.class);
        intent.putStringArrayListExtra(ARG_TRUCK_IDS, checkNotNull(truckIds));
        intent.putExtra(ARG_STARTING_TRUCK, checkNotNull(startingTruck));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        String startingTruck;
        List<String> truckIds;
        if (intent.getData() == null) {

            // We were launched directly
            truckIds = intent.getStringArrayListExtra(ARG_TRUCK_IDS);
            startingTruck = intent.getStringExtra(ARG_STARTING_TRUCK);
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
                    throw new URISyntaxException(javaUri.toString(), "Segment structure not what we expected");
                }
                startingTruck = segments[size - 1];
                truckIds = Collections.singletonList(startingTruck);
            } catch (URISyntaxException e) {
                Intent forwardingIntent = new Intent(Intent.ACTION_VIEW);
                forwardingIntent.setData(intent.getData());
                startActivity(forwardingIntent);
                finish();
                return;
            }
        }

        adapter = new TruckDetailsPagerAdapter(getSupportFragmentManager(), truckIds);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(adapter.getTruckPosition(startingTruck));
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
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(ARG_ENDING_TRUCK, adapter.getTruckId(viewPager.getCurrentItem()));
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onTriedToLoadInvalidTruck() {
        Toast.makeText(this, R.string.error_invalid_truck, Toast.LENGTH_LONG).show();
        finish();
    }
}
