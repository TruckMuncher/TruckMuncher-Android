package com.truckmuncher.truckmuncher.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.truckmuncher.App;

import javax.inject.Inject;

public final class SyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject
    TruckService truckService;
    @Inject
    MenuService menuService;
    @Inject
    AuthService authService;
    @Inject
    AccountManager accountManager;
    @Inject
    ApiExceptionResolver apiExceptionResolver;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        App.inject(context, this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

//        try {
//            syncTruckServingMode(provider, syncResult);
//            syncMenuItemAvailability(provider, syncResult);
//        } catch (RemoteException e) {
//            syncResult.databaseError = true;
//        }

        new TruckServingModeSyncTask(provider, truckService, apiExceptionResolver).execute(syncResult);
        new MenuItemAvailabilitySyncTask(provider, menuService, apiExceptionResolver).execute(syncResult);
    }

//    void syncMenuItemAvailability(ContentProviderClient provider, SyncResult syncResult) throws RemoteException {
//        Cursor cursor = provider.query(MenuItemEntry.buildDirty(), MenuItemAvailabilityQuery.PROJECTION, null, null, null);
//        if (!cursor.moveToFirst()) {
//            // Cursor is empty. Probably already synced this.
//            cursor.close();
//            return;
//        }
//
//        // Construct all the diffs for the request
//        final List<MenuItemAvailability> diff = new ArrayList<>(cursor.getCount());
//        do {
//            diff.add(
//                    new MenuItemAvailability.Builder()
//                            .menuItemId(cursor.getString(MenuItemAvailabilityQuery.INTERNAL_ID))
//                            .isAvailable(cursor.getInt(MenuItemAvailabilityQuery.IS_AVAILABLE) == 1)
//                            .build()
//            );
//        } while (cursor.moveToNext());
//        cursor.close();
//
//        // Setup and run the request synchronously
//        ModifyMenuItemAvailabilityRequest request = new ModifyMenuItemAvailabilityRequest(diff);
//        try {
//            menuService.modifyMenuItemAvailability(request);
//
//            // On a successful response, clear the dirty state, but only for values we synced. User might have changed others in the mean time.
//            ContentValues[] contentValues = new ContentValues[diff.size()];
//            for (int i = 0, max = diff.size(); i < max; i++) {
//                MenuItemAvailability availability = diff.get(i);
//                ContentValues values = new ContentValues();
//                values.put(MenuItemEntry.COLUMN_INTERNAL_ID, availability.menuItemId);
//                values.put(MenuItemEntry.COLUMN_IS_DIRTY, false);
//                contentValues[i] = values;
//            }
//
//            // Since we're clearing an internal state, don't notify listeners
//            Uri uri = Contract.buildSuppressNotify(MenuItemEntry.CONTENT_URI);
//            provider.bulkInsert(uri, contentValues);
//        } catch (ApiException e) {
//            handleApiException(e, syncResult);
//        }
//    }
//
//    void syncTruckServingMode(ContentProviderClient provider, SyncResult syncResult) throws RemoteException {
//        Cursor cursor = provider.query(TruckEntry.buildDirty(), TruckServingModeQuery.PROJECTION, null, null, null);
//        if (!cursor.moveToFirst()) {
//            // Cursor is empty. Probably already synced this.
//            cursor.close();
//            return;
//        }
//
//        do {
//            ServingModeRequest request = new ServingModeRequest.Builder()
//                    .isInServingMode(cursor.getInt(TruckServingModeQuery.IS_SERVING) == 1)
//                    .truckId(cursor.getString(TruckServingModeQuery.INTERNAL_ID))
//                    .truckLatitude(cursor.getDouble(TruckServingModeQuery.LATITUDE))
//                    .truckLongitude(cursor.getDouble(TruckServingModeQuery.LONGITUDE))
//                    .build();
//
//            try {
//                truckService.modifyServingMode(request);
//
//                // Clear the dirty state
//                ContentValues values = new ContentValues();
//                values.put(TruckEntry.COLUMN_IS_DIRTY, false);
//
//                // Since we're clearing an internal state, don't notify listeners
//                Uri uri = Contract.buildSuppressNotify(TruckEntry.buildSingleTruck(request.truckId));
//                provider.update(uri, values, null, null);
//            } catch (ApiException e) {
//                handleApiException(e, syncResult);
//            }
//        } while (cursor.moveToNext());
//        cursor.close();
//    }
//
//    private void handleApiException(ApiException exception, SyncResult syncResult) {
//        if (exception instanceof ExpiredSessionException) {
//            try {
//                AuthResponse response = authService.getAuth(new AuthRequest());
//                Account account = AccountGeneral.getStoredAccount(accountManager);
//                accountManager.setUserData(account, AuthenticatedRequestInterceptor.SESSION_TOKEN, response.sessionToken);
//                syncResult.fullSyncRequested = true;
//            } catch (SocialCredentialsException e) {
//                handleApiException(e, syncResult);
//            }
//        } else if (exception instanceof SocialCredentialsException) {
//            Bundle options = new Bundle();
//            options.putBoolean(Authenticator.ARG_NEEDS_SYNC, true);
//            Account account = AccountGeneral.getStoredAccount(accountManager);
//            accountManager.getAuthToken(
//                    account,
//                    AccountGeneral.AUTH_TOKEN_TYPE,
//                    options,
//                    true,
//                    null,
//                    null
//            );
//            syncResult.tooManyRetries = true;   // Don't bother trying the sync again until the user has logged in
//        } else {
//            Throwable t = exception.getCause();
//            if (t instanceof RetrofitError) {
//                RetrofitError error = (RetrofitError) t;
//                switch (error.getKind()) {
//                    case NETWORK:
//                        syncResult.stats.numIoExceptions++;
//                        break;
//                    case CONVERSION:
//                        syncResult.stats.numParseExceptions++;
//                        break;
//                    default:
//                    // If it was a server error, we either handle it elsewhere or a repeat request won't make a difference.
//                }
//            }
//        }
//    }

//    interface MenuItemAvailabilityQuery {
//        static final String[] PROJECTION = new String[]{
//                MenuItemEntry.COLUMN_INTERNAL_ID,
//                MenuItemEntry.COLUMN_IS_AVAILABLE
//        };
//        static final int INTERNAL_ID = 0;
//        static final int IS_AVAILABLE = 1;
//    }
//
//    interface TruckServingModeQuery {
//        static final String[] PROJECTION = new String[]{
//                TruckEntry.COLUMN_INTERNAL_ID,
//                TruckEntry.COLUMN_IS_SERVING,
//                TruckEntry.COLUMN_LATITUDE,
//                TruckEntry.COLUMN_LONGITUDE
//        };
//        static final int INTERNAL_ID = 0;
//        static final int IS_SERVING = 1;
//        static final int LATITUDE = 2;
//        static final int LONGITUDE = 3;
//    }
}
