package com.truckmuncher.app.vendor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sync.ApiExceptionResolver;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(ReadableRobolectricTestRunner.class)
public class VendorTruckStateResolverTest {

    @Mock
    TruckService truckService;
    @Mock
    ApiExceptionResolver exceptionResolver;
    @Mock
    SQLiteDatabase database;
    @Mock
    UserAccount userAccount;
    @Mock
    ContentResolver contentResolver;
    VendorTruckStateResolver resolver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        resolver = new VendorTruckStateResolver(truckService, exceptionResolver, database, userAccount, contentResolver, bus);
    }

    @Test
    public void resultsAreNotStoredWhenRequestCreatesNewTruck() {
        TrucksForVendorResponse response = new TrucksForVendorResponse.Builder()
                .isNew(true)
                .build();
        when(truckService.getTrucksForVendor(any(TrucksForVendorRequest.class))).thenReturn(response);

        resolver.resolveState();
        verifyZeroInteractions(database);
    }

    @Test
    public void oldDataGetsClearedAndNewDataGetsStored() {
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
        when(truckService.getTrucksForVendor(any(TrucksForVendorRequest.class))).thenReturn(response);
        when(userAccount.getUserId()).thenReturn("User ID");

        resolver.resolveState();

        ArgumentCaptor<String> tableCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ContentValues> valuesCaptor = ArgumentCaptor.forClass(ContentValues.class);
        ArgumentCaptor<String> whereClauseCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String[]> whereArgsCaptor = ArgumentCaptor.forClass(String[].class);
        verify(database, times(2)).update(tableCaptor.capture(), valuesCaptor.capture(), whereClauseCaptor.capture(), whereArgsCaptor.capture());

        // Make sure that all old items are cleared
        assertThat(tableCaptor.getAllValues().get(0)).isEqualTo(Tables.TRUCK_PROPERTIES);
        assertThat(valuesCaptor.getAllValues().get(0).getAsString(PublicContract.Truck.OWNER_ID)).isNull();
        assertThat(whereClauseCaptor.getAllValues().get(0)).isNull();
        assertThat(whereArgsCaptor.getAllValues().get(0)).isNull();

        // Make sure that the new item is added
        assertThat(tableCaptor.getAllValues().get(1)).isEqualTo(Tables.TRUCK_PROPERTIES);
        assertThat(valuesCaptor.getAllValues().get(1).getAsString(PublicContract.Truck.OWNER_ID)).isEqualTo("User ID");
        assertThat(whereClauseCaptor.getAllValues().get(1)).isEqualTo("id=?");
        assertThat(whereArgsCaptor.getAllValues().get(1)).containsExactly("ID");
    }
}
