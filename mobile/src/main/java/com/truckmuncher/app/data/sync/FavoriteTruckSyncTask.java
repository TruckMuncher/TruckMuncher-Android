package com.truckmuncher.app.data.sync;

import android.content.ContentValues;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.truckmuncher.api.user.FavoriteRequest;
import com.truckmuncher.api.user.FavoriteResponse;
import com.truckmuncher.api.user.GetFavoritesRequest;
import com.truckmuncher.api.user.UserService;
import com.truckmuncher.app.data.ApiException;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.data.sql.WhereClause;

import static com.truckmuncher.app.data.sql.WhereClause.Operator.EQUALS;

public class FavoriteTruckSyncTask extends SyncTask {

    private final SQLiteOpenHelper openHelper;
    private final UserService userService;
    private final ApiExceptionResolver exceptionResolver;

    public FavoriteTruckSyncTask(SQLiteOpenHelper openHelper, UserService userService, ApiExceptionResolver exceptionResolver) {
        this.openHelper = openHelper;
        this.userService = userService;
        this.exceptionResolver = exceptionResolver;
    }

    /**
     * The strategy for this sync is to:
     * <ol>
     * <li>Get all dirty records. These might represent a add favorite or a removed favorite.</li>
     * <li>Relay the add or remove to the API</li>
     * <li>Clear the dirty flag</li>
     * <li>Delete all clean records locally</li>
     * <li>Add all the server records</li>
     * </ol>
     * <p/>
     * What we're left with is a list of clean records that matches the server, and any dirty records that
     * we might have at this point
     */
    @NonNull
    @Override
    protected ApiResult sync(SyncResult syncResult) throws RemoteException {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        Cursor dirtyFavorites = getDirtyFavorites(db);

        // We want to keep track of the last response because it will contain the final server state
        FavoriteResponse lastResponse = null;
        try {
            lastResponse = uploadDirtyRecordsAndMarkAsClean(db, dirtyFavorites);
        } catch (ApiException e) {

            // Something isn't working. Could be a 1 off, could be bigger. Let the caller figure out what to do next
            return exceptionResolver.resolve(e);
        } finally {
            dirtyFavorites.close();
        }


        // TODO this could be eliminated if we had GCM
        // In this case there are no dirty items so we didn't make a network call and we need to get the state from the server
        if (lastResponse == null) {
            try {
                lastResponse = userService.getFavorites(new GetFavoritesRequest());
            } catch (ApiException e) {

                // At this point we don't have a response, so just exit and let the caller figure out if we should retry
                return exceptionResolver.resolve(e);
            }
        }

        try {

            // Want to use a transaction b/c we don't want to delete things if we have failures while inserting
            db.beginTransaction();

            // Delete all non-dirty items
            WhereClause deleteCleanWhere = WhereClause.where(Contract.FavoriteTruck.IS_DIRTY, EQUALS, false);
            db.delete(Tables.FAVORITE_TRUCK, deleteCleanWhere.selection, deleteCleanWhere.selectionArgs);

            ContentValues insertValues = new ContentValues();
            insertValues.put(Contract.FavoriteTruck.IS_FAVORITE, true);
            for (String truckId : lastResponse.favorites) {
                insertValues.put(Contract.FavoriteTruck.TRUCK_ID, truckId);

                // The insert means that trying to add a record which we already have (because it's still dirty) will fail.
                // We want this because our state wins over the server state
                db.insert(Tables.FAVORITE_TRUCK, null, insertValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return ApiResult.OK;
    }


    private Cursor getDirtyFavorites(SQLiteDatabase db) {
        WhereClause where = WhereClause.where(Contract.FavoriteTruck.IS_DIRTY, EQUALS, true);
        return db.query(Tables.FAVORITE_TRUCK, Query.PROJECTION, where.selection, where.selectionArgs, null, null, null);
    }

    @Nullable
    private FavoriteResponse uploadDirtyRecordsAndMarkAsClean(SQLiteDatabase db, Cursor dirtyFavorites) throws ApiException {

        // Going to be updating every dirty item the same way: by removing the dirty flag. Only need to construct the values once.
        ContentValues clearDirtyValues = new ContentValues();
        clearDirtyValues.put(Contract.FavoriteTruck.IS_DIRTY, false);

        FavoriteResponse lastResponse = null;

        while (dirtyFavorites.moveToNext()) {
            String truckId = dirtyFavorites.getString(Query.TRUCK_ID);
            FavoriteRequest request = new FavoriteRequest.Builder()
                    .truckId(truckId)
                    .build();

            if (dirtyFavorites.getInt(Query.IS_FAVORITE) == 1) {
                lastResponse = userService.addFavorite(request);
            } else {
                lastResponse = userService.removeFavorite(request);
            }

            WhereClause clearDirtyWhere = WhereClause.where(Contract.FavoriteTruck.TRUCK_ID, EQUALS, truckId);
            db.update(Tables.FAVORITE_TRUCK, clearDirtyValues, clearDirtyWhere.selection, clearDirtyWhere.selectionArgs);
        }
        return lastResponse;
    }

    interface Query {
        String[] PROJECTION = new String[]{
                Contract.FavoriteTruck.TRUCK_ID,
                Contract.FavoriteTruck.IS_FAVORITE
        };
        int TRUCK_ID = 0;
        int IS_FAVORITE = 1;
    }
}
