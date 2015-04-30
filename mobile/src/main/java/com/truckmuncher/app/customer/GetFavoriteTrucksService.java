 package com.truckmuncher.app.customer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;

import com.truckmuncher.api.user.FavoriteResponse;
import com.truckmuncher.api.user.GetFavoritesRequest;
import com.truckmuncher.api.user.UserService;
import com.truckmuncher.app.App;
import com.truckmuncher.app.data.ApiException;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sql.WhereClause;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class GetFavoriteTrucksService extends IntentService {

    public static final String ARG_MESSAGE = "user_message";

    @Inject
    UserService userService;
    @Inject
    SQLiteOpenHelper openHelper;

    public GetFavoriteTrucksService() {
        super(GetFavoriteTrucksService.class.getSimpleName());
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, GetFavoriteTrucksService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GetFavoritesRequest request = new GetFavoritesRequest.Builder().build();

        SQLiteDatabase db = openHelper.getWritableDatabase();

        try {
            FavoriteResponse response = userService.getFavorites(request);

            if (response == null) {
                return;
            }

            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(Contract.FavoriteTruck.IS_DIRTY, 1);
            values.put(Contract.FavoriteTruck.IS_FAVORITE, 0);

            // Mark all favorites in the database as dirty so we can remove out of date favorites
            db.update(Tables.FAVORITE_TRUCK, values, null, null);

            List<String> favoriteIds = response.favorites;

            for (int i = 0; i < favoriteIds.size(); i++) {
                String truckId = favoriteIds.get(i);
                values = new ContentValues();
                values.put(Contract.FavoriteTruck.TRUCK_ID, truckId);
                values.put(Contract.FavoriteTruck.IS_FAVORITE, 1);
                values.put(Contract.FavoriteTruck.IS_DIRTY, 0);

                db.replace(Tables.FAVORITE_TRUCK, null, values);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

            getContentResolver().notifyChange(PublicContract.TRUCK_URI, null);
        } catch (ApiException e) {
            Timber.e(e, "An error occurred while getting favorite trucks.");
            Intent errorIntent = new Intent();
            errorIntent.putExtra(ARG_MESSAGE, e.getMessage());
            LocalBroadcastManager.getInstance(GetFavoriteTrucksService.this).sendBroadcast(errorIntent);
        }
    }
}
