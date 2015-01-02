package com.truckmuncher.truckmuncher.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.truckmuncher.truckmuncher.LoggerStarter;
import com.truckmuncher.truckmuncher.data.sql.SqlOpenHelper;
import com.truckmuncher.truckmuncher.data.sql.Tables;
import com.truckmuncher.truckmuncher.menu.MenuUpdateService;

import timber.log.Timber;

public class TruckMuncherContentProvider extends ContentProvider {

    private static final int CATEGORY = 10;
    private static final int MENU_ITEM = 20;
    private static final int TRUCK = 30;
    private static final int TRUCK_STATE = 31;
    private static final int TRUCK_PROPERTIES = 32;
    private static final int MENU = 40;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    // TODO this is not OK. We need push messages
    private static boolean hasAlreadySyncedMenuThisSession = false;

    private SQLiteOpenHelper database;

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
        database = SqlOpenHelper.newInstance(getContext());
        return true;
    }

    @Override
    @NonNull
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
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
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented. Uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
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

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated;

        switch (uriMatcher.match(uri)) {
            case TRUCK_STATE:
                rowsUpdated = db.update(Tables.TRUCK_STATE, values, selection, selectionArgs);
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

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] valuesList) {
        SQLiteDatabase db = database.getWritableDatabase();
        int returnCount = 0;

        String tableName;
        switch (uriMatcher.match(uri)) {
            case MENU_ITEM:
                tableName = Tables.MENU_ITEM;
                break;
            case CATEGORY:
                tableName = Tables.CATEGORY;
                break;
            case TRUCK_PROPERTIES:
                tableName = Tables.TRUCK_PROPERTIES;
                uri = PublicContract.TRUCK_URI;
                break;
            case TRUCK_STATE:
                tableName = Tables.TRUCK_STATE;
                uri = PublicContract.TRUCK_URI;
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

        if (returnCount > 0 && !Contract.isSuppressNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null, Contract.isSyncToNetwork(uri));
        }

        return returnCount;
    }
}