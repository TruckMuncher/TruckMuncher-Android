package com.truckmuncher.app.vendor;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.test.VerifiableContentProvider;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(ReadableRobolectricTestRunner.class)
public class VendorTrucksServiceTest {

    @Test
    public void onCreateInjectsService() {
        VendorTrucksService service = new VendorTrucksService();
        assertThat(service.truckService).isNull();
        assertThat(service.authService).isNull();
        assertThat(service.accountManager).isNull();

        service.onCreate();
        assertThat(service.truckService).isNotNull();
        assertThat(service.authService).isNotNull();
        assertThat(service.accountManager).isNotNull();
    }

    @Test
    public void onHandleIntentPerformsBulkInsertWithFetchedData() {
        VendorTrucksService service = new VendorTrucksService();
        service.truckService = mock(TruckService.class);
        TrucksForVendorResponse response = new TrucksForVendorResponse.Builder()
                .trucks(Arrays.asList(new Truck.Builder()
                                        .id("ID")
                                        .name("My Truck")
                                        .imageUrl("http://truckmuncher/images/my_truck")
                                        .keywords(Arrays.asList("food"))
                                        .primaryColor("#000000")
                                        .secondaryColor("#FFFFFF")
                                        .build()
                        )
                )
                .isNew(false)
                .build();
        when(service.truckService.getTrucksForVendor(any(TrucksForVendorRequest.class)))
                .thenReturn(response);

        VerifiableContentProvider provider = new VerifiableContentProvider();
        ShadowContentResolver.registerProvider(PublicContract.CONTENT_AUTHORITY, provider);
        provider.enqueue(new VerifiableContentProvider.BulkInsertEvent() {
            @Override
            public int onBulkInsert(Uri uri, @NonNull ContentValues[] values) {
                assertThat(values[0].getAsString(PublicContract.Truck.ID)).isEqualTo("ID");
                assertThat(values[0].getAsString(PublicContract.Truck.NAME)).isEqualTo("My Truck");
                assertThat(values[0].getAsString(PublicContract.Truck.IMAGE_URL)).isEqualTo("http://truckmuncher/images/my_truck");
                assertThat(values[0].getAsString(PublicContract.Truck.KEYWORDS)).isEqualTo("food");
                assertThat(values[0].getAsString(PublicContract.Truck.COLOR_PRIMARY)).isEqualTo("#000000");
                assertThat(values[0].getAsString(PublicContract.Truck.COLOR_SECONDARY)).isEqualTo("#FFFFFF");
                return 0;
            }
        });
        provider.enqueue(new VerifiableContentProvider.BulkInsertEvent() {

            @Override
            public int onBulkInsert(Uri uri, @NonNull ContentValues[] values) {
                assertThat(values[0].getAsString(PublicContract.Truck.ID)).isEqualTo("ID");
                assertThat(values[0].getAsBoolean(PublicContract.Truck.OWNED_BY_CURRENT_USER)).isEqualTo(true);
                return 0;
            }
        });

        service.onHandleIntent(null);

        provider.assertThatCursorsAreClosed();
        provider.assertThatQueuesAreEmpty();
    }
}
