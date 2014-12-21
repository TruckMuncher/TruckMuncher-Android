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
import com.truckmuncher.truckmuncher.data.sql.CategoryTable;
import com.truckmuncher.truckmuncher.data.sql.MenuItemTable;
import com.truckmuncher.truckmuncher.data.sql.SqlOpenHelper;
import com.truckmuncher.truckmuncher.data.sql.TruckStateTable;
import com.truckmuncher.truckmuncher.data.sql.TruckTable;
import com.truckmuncher.truckmuncher.menu.MenuUpdateService;

import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.CONTENT_AUTHORITY;
import static com.truckmuncher.truckmuncher.data.Contract.CategoryEntry;
import static com.truckmuncher.truckmuncher.data.Contract.MenuItemEntry;
import static com.truckmuncher.truckmuncher.data.Contract.PATH_CATEGORY;
import static com.truckmuncher.truckmuncher.data.Contract.PATH_MENU;
import static com.truckmuncher.truckmuncher.data.Contract.PATH_MENU_ITEM;
import static com.truckmuncher.truckmuncher.data.Contract.TruckStateEntry;

public class MyContentProvider extends ContentProvider {

    private static final int TRUCK_SINGLE = 1;
    private static final int TRUCK_ALL = 2;
    private static final int CATEGORY_ALL = 4;
    private static final int MENU_ITEM_ALL = 6;
    private static final int MENU = 7;
    private static final int TRUCK_VIEW = 8;
    private static final int TRUCK_STATE = 9;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    // TODO this is not OK. We need push messages
    private static boolean hasAlreadySyncedMenuThisSession = false;

    private SQLiteOpenHelper database;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.TruckConstantEntry.TABLE_NAME, TRUCK_ALL);
        matcher.addURI(authority, Contract.TruckConstantEntry.TABLE_NAME + "/*", TRUCK_SINGLE);
        matcher.addURI(authority, Contract.TruckEntry.VIEW_NAME, TRUCK_VIEW);
        matcher.addURI(authority, TruckStateEntry.TABLE_NAME, TRUCK_STATE);

        matcher.addURI(authority, PATH_CATEGORY, CATEGORY_ALL);

        matcher.addURI(authority, PATH_MENU_ITEM, MENU_ITEM_ALL);

        matcher.addURI(authority, PATH_MENU, MENU);

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
            case MENU_ITEM_ALL:
                tableName = MenuItemEntry.TABLE_NAME;
                break;
            case TRUCK_VIEW:
                tableName = Contract.TruckEntry.VIEW_NAME;
                break;
            case MENU:
                tableName = Contract.MenuEntry.VIEW_NAME;

                // TODO replace with a push notification to spawn this sync
                if (Contract.isSyncFromNetwork(uri) && !hasAlreadySyncedMenuThisSession) {
                    getContext().startService(new Intent(getContext(), MenuUpdateService.class));
                    hasAlreadySyncedMenuThisSession = true;
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor retCursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    @NonNull
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TRUCK_SINGLE:
                return Contract.TruckEntry.CONTENT_ITEM_TYPE;
            case TRUCK_VIEW:
                return Contract.TruckEntry.CONTENT_TYPE;
            case CATEGORY_ALL:
                return CategoryEntry.CONTENT_TYPE;
            case MENU_ITEM_ALL:
                return MenuItemEntry.CONTENT_TYPE;
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
                rowsDeleted = db.delete(TruckStateEntry.TABLE_NAME, selection, selectionArgs);
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
                rowsUpdated = db.update(TruckStateEntry.TABLE_NAME, values, selection, selectionArgs);
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
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = database.getWritableDatabase();
        int returnCount;

        switch (uriMatcher.match(uri)) {
            case MENU_ITEM_ALL:
                returnCount = MenuItemTable.bulkInsert(db, values);
                break;
            case CATEGORY_ALL:
                returnCount = CategoryTable.bulkInsert(db, values);
                break;
            case TRUCK_ALL:
                returnCount = TruckTable.bulkInsert(db, values);
                uri = Contract.TruckEntry.CONTENT_URI;
                break;
            case TRUCK_STATE:
                returnCount = TruckStateTable.bulkInsert(db, values);
                uri = Contract.TruckEntry.CONTENT_URI;
                break;
            default:
                Timber.w("Attempting a bulk insert for an unsupported URI, %s. Falling back to normal inserts...", uri);
                returnCount = super.bulkInsert(uri, values);
        }

        if (returnCount > 0 && !Contract.isSuppressNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null, Contract.isSyncToNetwork(uri));
        }

        return returnCount;
    }
}