package com.truckmuncher.app.vendor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;

import com.squareup.wire.Wire;
import com.truckmuncher.api.auth.AuthRequest;
import com.truckmuncher.api.auth.AuthResponse;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.app.App;
import com.truckmuncher.app.authentication.AccountGeneral;
import com.truckmuncher.app.data.ApiException;
import com.truckmuncher.app.data.AuthenticatedRequestInterceptor;
import com.truckmuncher.app.data.ExpiredSessionException;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.SocialCredentialsException;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sql.WhereClause;

import javax.inject.Inject;

import timber.log.Timber;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class VendorTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";

    @Inject
    TruckService truckService;
    @Inject
    AuthService authService;
    @Inject
    AccountManager accountManager;
    @Inject
    SQLiteOpenHelper openHelper;

    public VendorTrucksService() {
        super(VendorTrucksService.class.getSimpleName());
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, VendorTrucksService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TrucksForVendorResponse response;
        try {
            response = truckService.getTrucksForVendor(new TrucksForVendorRequest());
        } catch (SocialCredentialsException sce) {
            // TODO Implement
            throw new UnsupportedOperationException("not yet implemented");
//            Account account = AccountGeneral.getStoredAccount(accountManager);
//            accountManager.getAuthToken(
//                    account,
//                    AccountGeneral.AUTH_TOKEN_TYPE,
//                    null,
//                    true,
//                    null,
//                    null
//            );
        } catch (ExpiredSessionException ese) {
            AuthResponse authResponse = authService.getAuth(new AuthRequest());
            Account account = AccountGeneral.getStoredAccount(accountManager);
            accountManager.setUserData(account, AuthenticatedRequestInterceptor.SESSION_TOKEN, authResponse.sessionToken);
            startService(intent);   // Start self
            return;
        } catch (ApiException e) {
            Timber.e("Got an error while getting trucks for vendor.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(this).sendBroadcast(errorIntent);
            return;
        }

        if (Wire.get(response.isNew, TrucksForVendorResponse.DEFAULT_ISNEW)) {

            // A new truck is the equivalent of not having one. Let the logic where we fetch all trucks handle this case.
            return;
        }

        SQLiteDatabase database = openHelper.getWritableDatabase();
        try {
            try {
                database.beginTransaction();

                // We need to clear the state of any trucks that were previously assigned to us since they might no longer be.
                // Only then can we assign the new trucks.
                ContentValues ownerColumn = new ContentValues(1);
                ownerColumn.put(PublicContract.Truck.OWNED_BY_CURRENT_USER, false);
                database.update(Tables.TRUCK_STATE, ownerColumn, null, null);

                // Now that we have valid trucks that belong to us, assign them to the user. Other parts of the system
                // already take care of keep truck data fresh. It's sent in the response only because the web needs it.
                ownerColumn = new ContentValues(1);
                ownerColumn.put(PublicContract.Truck.OWNED_BY_CURRENT_USER, true);
                for (Truck truck : response.trucks) {
                    WhereClause where = new WhereClause.Builder()
                            .where(PublicContract.Truck.ID, EQUALS, truck.id)
                            .build();
                    database.update(Tables.TRUCK_STATE, ownerColumn, where.selection, where.selectionArgs);
                }

                database.setTransactionSuccessful();
                getContentResolver().notifyChange(PublicContract.TRUCK_URI, null);
            } finally {
                database.endTransaction();
            }
        } finally {
            database.close();
        }
    }
}
