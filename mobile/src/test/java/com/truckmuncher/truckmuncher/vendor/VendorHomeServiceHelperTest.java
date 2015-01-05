package com.truckmuncher.truckmuncher.vendor;

import android.content.ContentValues;
import android.location.Location;
import android.net.Uri;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.PublicContract;
import com.truckmuncher.truckmuncher.test.VerifiableContentProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class VendorHomeServiceHelperTest {

    @Test
    public void changeServingStateUpdatesWithSyncToNetworkDirective() throws InterruptedException {
        VendorHomeServiceHelper helper = new VendorHomeServiceHelper();
        Location location = new Location("");
        location.setAltitude(43);
        location.setLongitude(53);

        final CountDownLatch latch = new CountDownLatch(1);
        VerifiableContentProvider provider = new VerifiableContentProvider();
        ShadowContentResolver.registerProvider(PublicContract.CONTENT_AUTHORITY, provider);
        provider.enqueue(new VerifiableContentProvider.UpdateEvent() {
            @Override
            public int onUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                assertThat(uri).isEqualTo(Contract.syncToNetwork(Contract.TRUCK_STATE_URI));
                assertThat(selection).isEqualTo("id=?");
                assertThat(selectionArgs).containsExactly("truckId");
                assertThat(values.getAsDouble(PublicContract.Truck.LATITUDE)).isEqualTo(43);
                assertThat(values.getAsDouble(PublicContract.Truck.LONGITUDE)).isEqualTo(53);
                assertThat(values.getAsBoolean(PublicContract.Truck.IS_SERVING)).isTrue();
                assertThat(values.getAsBoolean(Contract.TruckState.IS_DIRTY)).isTrue();

                latch.countDown();
                return 1;
            }
        });

        helper.changeServingState(Robolectric.application, "truckId", true, location);

        latch.await(3, TimeUnit.SECONDS);
        assertThat(latch.getCount()).isZero();
        provider.assertThatCursorsAreClosed();
        provider.assertThatQueuesAreEmpty();
    }
}
