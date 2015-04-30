package com.truckmuncher.app.customer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.truckmuncher.app.App;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.data.sql.Tables;

import javax.inject.Inject;

public class RemoveFavoriteTruckService extends IntentService {

    private static final String ARG_TRUCK_ID = "truck_id";

    @Inject
    SQLiteOpenHelper openHelper;

    public RemoveFavoriteTruckService() {
        super(AddFavoriteTruckService.class.getName());
    }

    public static Intent newIntent(Context context, String truckId) {
        Intent intent = new Intent(context, RemoveFavoriteTruckService.class);
        intent.putExtra(ARG_TRUCK_ID, truckId);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String truckId = intent.getStringExtra(ARG_TRUCK_ID);

        ContentValues values = new ContentValues();
        values.put(Contract.FavoriteTruck.TRUCK_ID, truckId);
        values.put(Contract.FavoriteTruck.IS_FAVORITE, 0);
        values.put(Contract.FavoriteTruck.IS_DIRTY, 1);

        db.replace(Tables.FAVORITE_TRUCK, null, values);

        getContentResolver().notifyChange(PublicContract.TRUCK_URI, null, true);
    }
}
