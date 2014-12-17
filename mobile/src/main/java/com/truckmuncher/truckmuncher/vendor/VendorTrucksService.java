package com.truckmuncher.truckmuncher.vendor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.auth.AuthRequest;
import com.truckmuncher.api.auth.AuthResponse;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.api.trucks.TrucksForVendorRequest;
import com.truckmuncher.api.trucks.TrucksForVendorResponse;
import com.truckmuncher.truckmuncher.App;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;
import com.truckmuncher.truckmuncher.data.ApiException;
import com.truckmuncher.truckmuncher.data.AuthenticatedRequestInterceptor;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.ExpiredSessionException;
import com.truckmuncher.truckmuncher.data.SocialCredentialsException;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class VendorTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";

    @Inject
    TruckService truckService;
    @Inject
    AuthService authService;
    @Inject
    AccountManager accountManager;

    public VendorTrucksService() {
        super(VendorTrucksService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        App.inject(this, this);
        try {
            TrucksForVendorResponse response = truckService.getTrucksForVendor(new TrucksForVendorRequest());

            List<Truck> trucks = response.trucks;
            ContentValues[] contentValues = new ContentValues[trucks.size()];
            for (int i = 0, max = trucks.size(); i < max; i++) {
                Truck truck = trucks.get(i);
                ContentValues values = new ContentValues();
                values.put(Contract.TruckConstantEntry.COLUMN_INTERNAL_ID, truck.id);
                values.put(Contract.TruckConstantEntry.COLUMN_NAME, truck.name);
                values.put(Contract.TruckConstantEntry.COLUMN_IMAGE_URL, truck.imageUrl);
                values.put(Contract.TruckConstantEntry.COLUMN_KEYWORDS, Contract.convertListToString(truck.keywords));
                values.put(Contract.TruckConstantEntry.COLUMN_COLOR_PRIMARY, truck.primaryColor);
                values.put(Contract.TruckConstantEntry.COLUMN_COLOR_SECONDARY, truck.secondaryColor);
                values.put(Contract.TruckConstantEntry.COLUMN_OWNED_BY_CURRENT_USER, true);
                contentValues[i] = values;
            }

            getContentResolver().bulkInsert(Contract.TruckConstantEntry.CONTENT_URI, contentValues);
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
            AuthResponse response = authService.getAuth(new AuthRequest());
            Account account = AccountGeneral.getStoredAccount(accountManager);
            accountManager.setUserData(account, AuthenticatedRequestInterceptor.SESSION_TOKEN, response.sessionToken);
            startService(intent);   // Start self
        } catch (ApiException e) {
            Timber.e("Got an error while getting trucks for vendor.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(VendorTrucksService.this).sendBroadcast(errorIntent);
        }
    }
}
