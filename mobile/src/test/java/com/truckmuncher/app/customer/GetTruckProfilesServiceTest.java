package com.truckmuncher.app.customer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckProfilesRequest;
import com.truckmuncher.api.trucks.TruckProfilesResponse;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sql.TestOpenHelper;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(ReadableRobolectricTestRunner.class)
public class GetTruckProfilesServiceTest {

    @Mock
    TruckService truckService;
    SQLiteOpenHelper openHelper;

    GetTruckProfilesService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        openHelper = new TestOpenHelper(Robolectric.application);
        service = new GetTruckProfilesService();
        service.truckService = truckService;
        service.openHelper = openHelper;
    }

    @Test
    public void unapprovedTrucksAreIgnored() {
        TruckProfilesResponse response = new TruckProfilesResponse.Builder()
                .trucks(Arrays.asList(
                        new Truck.Builder()
                                .id("ID")
                                .name("Truck name")
                                .imageUrl("http://image.url")
                                .keywords(Arrays.asList("key", "words"))
                                .primaryColor("#FFF")
                                .secondaryColor("#000")
                                .description("A thorough truck")
                                .phoneNumber("(867) 530-9000")
                                .website("http://donthaveone.com")
                                .approved(false)
                                .build()
                )).build();
        when(truckService.getTruckProfiles(any(TruckProfilesRequest.class))).thenReturn(response);

        service.onHandleIntent(new Intent());

        SQLiteDatabase db = openHelper.getReadableDatabase();
        assertThat(db.query(Tables.TRUCK_PROPERTIES, null, null, null, null, null, null).getCount()).isZero();
    }

    @Test
    public void newTrucksAreInserted() {
        TruckProfilesResponse response = new TruckProfilesResponse.Builder()
                .trucks(Arrays.asList(
                        new Truck.Builder()
                                .id("ID")
                                .name("Truck name")
                                .imageUrl("http://image.url")
                                .keywords(Arrays.asList("key", "words"))
                                .primaryColor("#FFF")
                                .secondaryColor("#000")
                                .description("A thorough truck")
                                .phoneNumber("(867) 530-9000")
                                .website("http://donthaveone.com")
                                .approved(true)
                                .build()
                )).build();
        when(truckService.getTruckProfiles(any(TruckProfilesRequest.class))).thenReturn(response);

        service.onHandleIntent(new Intent());

        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor c = db.query(Tables.TRUCK_PROPERTIES, null, null, null, null, null, null);
        assertThat(c.moveToFirst()).isTrue();
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.ID))).isEqualTo("ID");
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.NAME))).isEqualTo("Truck name");
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.IMAGE_URL))).isEqualTo("http://image.url");
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.KEYWORDS))).isEqualTo(PublicContract.convertListToString(Arrays.asList("key", "words")));
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.COLOR_PRIMARY))).isEqualTo("#FFF");
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.COLOR_SECONDARY))).isEqualTo("#000");
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.DESCRIPTION))).isEqualTo("A thorough truck");
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.PHONE_NUMBER))).isEqualTo("(867) 530-9000");
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.WEBSITE))).isEqualTo("http://donthaveone.com");
        c.close();
    }

    @Test
    public void oldTrucksAreRemoved() {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        // Insert some "old" data
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "OldId");
        values.put(PublicContract.Truck.NAME, "Old Truck Name");
        db.insert(Tables.TRUCK_PROPERTIES, null, values);

        TruckProfilesResponse response = new TruckProfilesResponse.Builder()
                .trucks(Arrays.asList(
                        new Truck.Builder()
                                .id("ID")
                                .name("Truck name")
                                .imageUrl("http://image.url")
                                .keywords(Arrays.asList("key", "words"))
                                .primaryColor("#FFF")
                                .secondaryColor("#000")
                                .description("A thorough truck")
                                .phoneNumber("(867) 530-9000")
                                .website("http://donthaveone.com")
                                .approved(true)
                                .build()
                )).build();
        when(truckService.getTruckProfiles(any(TruckProfilesRequest.class))).thenReturn(response);

        service.onHandleIntent(new Intent());

        Cursor c = db.query(Tables.TRUCK_PROPERTIES, null, null, null, null, null, null);
        assertThat(c.getCount()).isEqualTo(1);
        assertThat(c.moveToFirst()).isTrue();
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.ID))).isEqualTo("ID");
        c.close();
    }

    @Test
    public void existingTrucksAreUpdated() {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        // Insert some "existing" data
        ContentValues values = new ContentValues();
        values.put(PublicContract.Truck.ID, "ID");
        values.put(PublicContract.Truck.NAME, "Existing Truck Name");
        db.insert(Tables.TRUCK_PROPERTIES, null, values);

        TruckProfilesResponse response = new TruckProfilesResponse.Builder()
                .trucks(Arrays.asList(
                        new Truck.Builder()
                                .id("ID")
                                .name("Truck name")
                                .imageUrl("http://image.url")
                                .keywords(Arrays.asList("key", "words"))
                                .primaryColor("#FFF")
                                .secondaryColor("#000")
                                .description("A thorough truck")
                                .phoneNumber("(867) 530-9000")
                                .website("http://donthaveone.com")
                                .approved(true)
                                .build()
                )).build();
        when(truckService.getTruckProfiles(any(TruckProfilesRequest.class))).thenReturn(response);

        service.onHandleIntent(new Intent());

        Cursor c = db.query(Tables.TRUCK_PROPERTIES, null, null, null, null, null, null);
        assertThat(c.getCount()).isEqualTo(1);
        assertThat(c.moveToFirst()).isTrue();
        assertThat(c.getString(c.getColumnIndexOrThrow(PublicContract.Truck.NAME))).isEqualTo("Truck name");
        c.close();
    }
}
