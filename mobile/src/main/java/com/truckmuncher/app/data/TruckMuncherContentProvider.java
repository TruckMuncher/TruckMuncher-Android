package com.truckmuncher.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.truckmuncher.app.App;
import com.truckmuncher.app.common.LoggerStarter;
import com.truckmuncher.app.data.sql.Tables;
import com.truckmuncher.app.menu.MenuUpdateService;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class TruckMuncherContentProvider extends ContentProvider {

    public static final String METHOD_UPDATE_INACTIVE_TRUCKS = "method_update_inactive_trucks";
    public static final String METHOD_UPDATE_SEARCH_RESULTS = "method_update_search_results";
    public static final String METHOD_CLEAR_SEARCH_RESULTS = "method_clear_search_results";
    public static final String ARG_ID_ARRAY = "arg_id_list";

    private static final int CATEGORY = 10;
    private static final int MENU_ITEM = 20;
    private static final int TRUCK = 30;
    private static final int TRUCK_STATE = 31;
    private static final int TRUCK_PROPERTIES = 32;
    private static final int MENU = 40;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    // TODO this is not OK. We need push messages
    private static boolean hasAlreadySyncedMenuThisSession = false;

    @Inject
    SQLiteOpenHelper database;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = PublicContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "/category", CATEGORY);
        matcher.addURI(authority, "/menu_item", MENU_ITEM);
        matcher.addURI(authority, "/truck", TRUCK);
        matcher.addURI(authority, "/truck_state", TRUCK_STATE);
        matcher.addURI(authority, "/truck_properties", TRUCK_PROPERTIES);
        matcher.addURI(authority, "/menu", MENU);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        LoggerStarter.start(getContext());
        return true;
    }

    @Override
    public Bundle call(@NonNull String method, String arg, Bundle extras) {
        if (database == null) {
            App.get(getContext()).inject(this);
        }
        SQLiteDatabase db = database.getReadableDatabase();
        SQLiteStatement statement;
        String[] ids = extras != null ? extras.getStringArray(ARG_ID_ARRAY) : new String[]{};

        switch (method) {
            case METHOD_UPDATE_INACTIVE_TRUCKS:
                statement = db.compileStatement("UPDATE " + Tables.TRUCK_STATE +
                        " SET " + PublicContract.Truck.IS_SERVING + " = CASE WHEN " +
                        PublicContract.Truck.ID + " not in (" + generatePlaceholders(ids.length) + ") THEN 0 ELSE 1 END;");

                for (int i = 0; i < ids.length; i++) {
                    statement.bindString(i + 1, ids[i]);
                }
                break;
            case METHOD_UPDATE_SEARCH_RESULTS:
                statement = db.compileStatement("UPDATE " + Tables.TRUCK_STATE +
                        " SET " + PublicContract.Truck.MATCHED_SEARCH + " = CASE WHEN " +
                        PublicContract.Truck.ID + " not in (" + generatePlaceholders(ids.length) + ") THEN 0 ELSE 1 END;");

                for (int i = 0; i < ids.length; i++) {
                    statement.bindString(i + 1, ids[i]);
                }
                break;

            case METHOD_CLEAR_SEARCH_RESULTS:
                statement = db.compileStatement("UPDATE " + Tables.TRUCK_STATE +
                        " SET " + PublicContract.Truck.MATCHED_SEARCH + " = 1");
                break;
            default:
                return super.call(method, arg, extras);
        }

        statement.execute();

        getContext().getContentResolver().notifyChange(PublicContract.TRUCK_URI, null);


        return null;
    }

    @DebugLog
    @Override
    @NonNull
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (database == null) {
            App.get(getContext()).inject(this);
        }
        SQLiteDatabase db = database.getReadableDatabase();

        String tableName;
        switch (uriMatcher.match(uri)) {
            case CATEGORY:
                tableName = Tables.CATEGORY;
                break;
            case MENU_ITEM:
                tableName = Tables.MENU_ITEM;
                break;
            case TRUCK:
                tableName = Tables.TRUCK;
                break;
            case TRUCK_STATE:
                tableName = Tables.TRUCK_STATE;
                break;
            case TRUCK_PROPERTIES:
                tableName = Tables.TRUCK_PROPERTIES;
                break;
            case MENU:
                tableName = Tables.MENU;

                // TODO replace with a push notification to spawn this sync
                if (Contract.isSyncFromNetwork(uri) && !hasAlreadySyncedMenuThisSession) {
                    getContext().startService(new Intent(getContext(), MenuUpdateService.class));
                    hasAlreadySyncedMenuThisSession = true;
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor retCursor = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @DebugLog
    @Override
    @NonNull
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CATEGORY:
                return PublicContract.URI_TYPE_CATEGORY;
            case MENU_ITEM:
                return PublicContract.URI_TYPE_MENU_ITEM;
            case TRUCK:
                return PublicContract.URI_TYPE_TRUCK;
            case MENU:
                return PublicContract.URI_TYPE_MENU;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /**
     * @return null if the insert failed. Otherwise the same uri as was provided.
     */
    @DebugLog
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented. Uri: " + uri);
    }

    @DebugLog
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (database == null) {
            App.get(getContext()).inject(this);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case TRUCK_STATE:
                rowsDeleted = db.delete(Tables.TRUCK_STATE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0 && !Contract.isSuppressNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null, Contract.isSyncToNetwork(uri));
        }

        return rowsDeleted;
    }

    @DebugLog
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (database == null) {
            App.get(getContext()).inject(this);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated;

        switch (uriMatcher.match(uri)) {
            case TRUCK_STATE:
                rowsUpdated = db.update(Tables.TRUCK_STATE, values, selection, selectionArgs);
                break;
            case MENU_ITEM:
                rowsUpdated = db.update(Tables.MENU_ITEM, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        // Notify and sync as appropriate. This is defined by the Uri
        if (rowsUpdated != 0 && !Contract.isSuppressNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null, Contract.isSyncToNetwork(uri));
        }

        return rowsUpdated;
    }

    @DebugLog
    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] valuesList) {
        if (database == null) {
            App.get(getContext()).inject(this);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        int returnCount = 0;
        boolean suppressNotification = Contract.isSuppressNotify(uri);

        String tableName;
        switch (uriMatcher.match(uri)) {
            case MENU_ITEM:
                tableName = Tables.MENU_ITEM;
                break;
            case CATEGORY:
                tableName = Tables.CATEGORY;
                break;
            case TRUCK_STATE:
                tableName = Tables.TRUCK_STATE;
                uri = suppressNotification ? Contract.suppressNotify(PublicContract.TRUCK_URI) : PublicContract.TRUCK_URI;
                break;
            default:
                Timber.w(new UnsupportedOperationException(), "Attempting a bulk insert for an unsupported URI, %s. Falling back to normal inserts...", uri);
                returnCount = super.bulkInsert(uri, valuesList);
                tableName = null;
        }

        if (tableName != null) {    // We have bulkInsert support for the uri
            db.beginTransaction();
            try {
                for (ContentValues values : valuesList) {
                    long rowId = db.replace(tableName, null, values);
                    if (rowId != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        if (returnCount > 0 && !suppressNotification) {
            getContext().getContentResolver().notifyChange(uri, null, Contract.isSyncToNetwork(uri));
        }

        return returnCount;
    }

    private String generatePlaceholders(int number) {
        if (number < 1) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder(number * 2 - 1);
            builder.append("?");

            for (int i = 1; i < number; i++) {
                builder.append(",?");
            }

            return builder.toString();
        }
    }
}