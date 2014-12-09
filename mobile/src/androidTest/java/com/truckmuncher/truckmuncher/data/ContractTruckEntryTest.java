package com.truckmuncher.truckmuncher.data;

import android.net.Uri;

import junit.framework.TestCase;

import java.util.UUID;

import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class ContractTruckEntryTest extends TestCase {

    public void testSingleTruckConversion() {
        String internalId = UUID.randomUUID().toString();
        Uri uri = TruckEntry.buildSingleTruck(internalId);

        assertThat(TruckEntry.getInternalIdFromUri(uri)).isEqualTo(internalId);
    }

    public void testGetInternalIdThrowsOnInvalidUri() {
        try {
            TruckEntry.getInternalIdFromUri(TruckEntry.CONTENT_URI);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // No-op
        }
    }

    public void testBuildSingleTruckThrowsOnInvalidId() {
        try {
            TruckEntry.buildSingleTruck("not_uuid");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // No-op
        }
    }

    public void testBuildServingTrucksUri() {
        Uri uri = TruckEntry.buildServingTrucks();

        String parameter = uri.getQueryParameter(TruckEntry.COLUMN_IS_SERVING);

        assertThat(uri.getPath()).isEqualTo("/" + Contract.PATH_TRUCK);
        assertThat(parameter).isEqualTo("1");
    }
}
