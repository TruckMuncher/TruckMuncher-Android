package com.truckmuncher.app.vendor;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.WhereClause;

import java.util.ArrayList;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

;


public class ResetVendorTrucksService extends IntentService {

    private static final String ARG_VALUES = "content_values";

    public ResetVendorTrucksService() {
        super(ResetVendorTrucksService.class.getSimpleName());
    }

    public static Intent newIntent(Context context, String[] truckIds) {
        Intent intent = new Intent(context, ResetVendorTrucksService.class);
        intent.putExtra(ARG_VALUES, truckIds);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String[] truckIds = intent.getStringArrayExtra(ARG_VALUES);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (String id : truckIds) {
            WhereClause whereClause = new WhereClause.Builder()
                    .where(PublicContract.Truck.ID, EQUALS, id)
                    .build();

            ops.add(ContentProviderOperation.newUpdate(Contract.TRUCK_STATE_URI)
                    .withSelection(whereClause.selection, whereClause.selectionArgs)
                    .withValue(PublicContract.Truck.OWNED_BY_CURRENT_USER, false)
                    .build());
        }

        try {
            getContentResolver().applyBatch(PublicContract.CONTENT_AUTHORITY, ops);
        } catch (RemoteException e) {
            // shouldn't happen
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }
}
