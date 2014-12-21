package com.truckmuncher.truckmuncher.data;

import android.net.Uri;

import com.truckmuncher.truckmuncher.data.sql.Query;

import junit.framework.TestCase;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class ContractTruckEntryTest extends TestCase {

    public void testSingleTruckConversion() {
        String internalId = UUID.randomUUID().toString();
        Query query = Contract.TruckEntry.buildSingleTruck(internalId);

        assertThat(Contract.TruckEntry.getInternalIdFromUri(uri)).isEqualTo(internalId);
    }

    public void testGetInternalIdThrowsOnInvalidUri() {
        try {
            Contract.TruckConstantEntry.getInternalIdFromUri(Contract.TruckConstantEntry.CONTENT_URI);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // No-op
        }
    }

    public void testBuildSingleTruckThrowsOnInvalidId() {
        try {
            Contract.TruckConstantEntry.buildSingleTruck("not_uuid");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // No-op
        }
    }

    public void testBuildServingTrucksUri() {
        Uri uri = Contract.TruckConstantEntry.buildServingTrucks();

        String parameter = uri.getQueryParameter(Contract.TruckConstantEntry.COLUMN_IS_SERVING);

        assertThat(uri.getPath()).isEqualTo("/" + Contract.PATH_TRUCK);
        assertThat(parameter).isEqualTo("1");
    }
}
