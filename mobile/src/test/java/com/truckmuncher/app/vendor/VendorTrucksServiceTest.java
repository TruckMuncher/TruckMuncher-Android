package com.truckmuncher.app.vendor;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.Tables;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(ReadableRobolectricTestRunner.class)
public class VendorTrucksServiceTest {

    @Mock
    TruckService truckService;
    @Mock
    SQLiteOpenHelper openHelper;
    VendorTrucksService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new VendorTrucksService();
        service.truckService = truckService;
        service.openHelper = openHelper;
    }

    @Test
    public void resultsAreNotStoredWhenRequestCreatesNewTruck() {
        TrucksForVendorResponse response = new TrucksForVendorResponse.Builder()
                .isNew(true)
                .build();
        when(truckService.getTrucksForVendor(any(TrucksForVendorRequest.class))).thenReturn(response);
        verifyZeroInteractions(openHelper);
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
        when(service.truckService.getTrucksForVendor(any(TrucksForVendorRequest.class))).thenReturn(response);

        SQLiteDatabase db = mock(SQLiteDatabase.class);
        when(openHelper.getWritableDatabase()).thenReturn(db);

        service.onHandleIntent(null);

        ArgumentCaptor<String> tableCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ContentValues> valuesCaptor = ArgumentCaptor.forClass(ContentValues.class);
        ArgumentCaptor<String> whereClauseCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String[]> whereArgsCaptor = ArgumentCaptor.forClass(String[].class);
        verify(db, times(2)).update(tableCaptor.capture(), valuesCaptor.capture(), whereClauseCaptor.capture(), whereArgsCaptor.capture());

        // Make sure that all old items are cleared
        assertThat(tableCaptor.getAllValues().get(0)).isEqualTo(Tables.TRUCK_STATE);
        assertThat(valuesCaptor.getAllValues().get(0).getAsBoolean(PublicContract.Truck.OWNED_BY_CURRENT_USER)).isFalse();
        assertThat(whereClauseCaptor.getAllValues().get(0)).isNull();
        assertThat(whereArgsCaptor.getAllValues().get(0)).isNull();

        // Make sure that the new item is added
        assertThat(tableCaptor.getAllValues().get(1)).isEqualTo(Tables.TRUCK_STATE);
        assertThat(valuesCaptor.getAllValues().get(1).getAsBoolean(PublicContract.Truck.OWNED_BY_CURRENT_USER)).isTrue();
        assertThat(whereClauseCaptor.getAllValues().get(1)).isEqualTo("id=?");
        assertThat(whereArgsCaptor.getAllValues().get(1)).containsExactly("ID");
    }
}
