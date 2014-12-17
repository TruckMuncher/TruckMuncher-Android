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
import com.truckmuncher.truckmuncher.data.sql.MenuView;
import com.truckmuncher.truckmuncher.data.sql.SqlOpenHelper;
import com.truckmuncher.truckmuncher.data.sql.TruckStateTable;
import com.truckmuncher.truckmuncher.data.sql.TruckTable;
import com.truckmuncher.truckmuncher.menu.MenuUpdateService;

import hugo.weaving.DebugLog;
import timber.log.Timber;

import static com.truckmuncher.truckmuncher.data.Contract.CONTENT_AUTHORITY;
import static com.truckmuncher.truckmuncher.data.Contract.CategoryEntry;
import static com.truckmuncher.truckmuncher.data.Contract.MenuItemEntry;
import static com.truckmuncher.truckmuncher.data.Contract.PATH_CATEGORY;
import static com.truckmuncher.truckmuncher.data.Contract.PATH_MENU;
import static com.truckmuncher.truckmuncher.data.Contract.PATH_MENU_ITEM;
import static com.truckmuncher.truckmuncher.data.Contract.TruckCombo;
import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;
import static com.truckmuncher.truckmuncher.data.Contract.TruckStateEntry;
import static com.truckmuncher.truckmuncher.data.Contract.needsSync;
import static com.truckmuncher.truckmuncher.data.Contract.sanitize;
import static com.truckmuncher.truckmuncher.data.Contract.suppressNotify;

public class MyContentProvider extends ContentProvider {

    private static final int TRUCK_SINGLE = 1;
    private static final int TRUCK_ALL = 2;
    private static final int CATEGORY_ALL = 4;
    private static final int MENU_ITEM_ALL = 6;
    private static final int MENU = 7;
    private static final int TRUCK_VIEW = 8;
    private static final int TRUCK_STATE = 9;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private SQLiteOpenHelper database;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, TruckEntry.TABLE_NAME, TRUCK_ALL);
        matcher.addURI(authority, TruckEntry.TABLE_NAME + "/*", TRUCK_SINGLE);
        matcher.addURI(authority, TruckCombo.VIEW_NAME, TRUCK_VIEW);
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

    @DebugLog
    @Override
    @NonNull
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = database.getReadableDatabase();
        final Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case MENU_ITEM_ALL:
                retCursor = MenuItemTable.queryMany(db, uri, projection);
                break;
            case TRUCK_VIEW:
                retCursor = db.query(TruckCombo.VIEW_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case TRUCK_ALL:
                throw new RuntimeException();

                // TODO Migrate to using selection and args. Just here for backwards compatibility
//                QueryArgs args = new QueryArgs(uri);
//                retCursor = db.query(TruckCombo.VIEW_NAME, projection, args.selection, args.selectionArgs, null, null, null);
//                break;
            case TRUCK_SINGLE:

                // TODO Migrate to using selection and args. Just here for backwards compatibility
                throw new RuntimeException();
//                selection = TruckEntry.COLUMN_INTERNAL_ID + "=?";
//                selectionArgs = new String[]{TruckCombo.getInternalIdFromUri(uri)};
//                retCursor = db.query(TruckCombo.VIEW_NAME, projection, selection, selectionArgs, null, null, null);
//                break;
            case MENU:
                Uri sanitized = sanitize(uri);
                retCursor = MenuView.queryMany(db, sanitized, projection);

                // TODO replace with a push notification to spawn this sync
                if (needsSync(uri)) {
                    getContext().startService(new Intent(getContext(), MenuUpdateService.class));
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    @NonNull
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TRUCK_SINGLE:
                return TruckCombo.CONTENT_ITEM_TYPE;
            case TRUCK_VIEW:
                return TruckCombo.CONTENT_TYPE;
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
        SQLiteDatabase db = database.getWritableDatabase();
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            default:
                throw new UnsupportedOperationException("Not yet implemented. Uri: " + uri.toString());
        }
//        if (!Contract.suppressNotify(uri)) {
//            getContext().getContentResolver().notifyChange(uri, null, Contract.needsSync(uri));
//        }
//        return returnUri;
    }

    @DebugLog
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        boolean suppressNotify = suppressNotify(uri);
        boolean needsSync = needsSync(uri);
        uri = sanitize(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted;
        switch (uriMatcher.match(uri)) {
            case TRUCK_ALL:

                throw new RuntimeException();
                // TODO Migrate to using selection and args. Just here for backwards compatibility
//                QueryArgs args = new QueryArgs(uri);
//                rowsDeleted = db.delete(TruckCombo.VIEW_NAME, args.selection, args.selectionArgs);
//                break;

                // TODO Is there any reason to have this?
//            case TRUCK_SINGLE:
//
//                // TODO Migrate to using selection and args. Just here for backwards compatibility
//                selection = TruckEntry.COLUMN_INTERNAL_ID + "=?";
//                selectionArgs = new String[]{TruckCombo.getInternalIdFromUri(uri)};
//                rowsDeleted = db.delete(TruckCombo.VIEW_NAME, selection, selectionArgs);
//                break;
            case TRUCK_STATE:
                rowsDeleted = db.delete(TruckStateEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented. Uri: " + uri.toString());
        }

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0 && !suppressNotify) {
            getContext().getContentResolver().notifyChange(uri, null, needsSync);
        }
        return rowsDeleted;
    }

    @DebugLog
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        boolean suppressNotify = suppressNotify(uri);
        boolean needsSync = needsSync(uri);
        uri = sanitize(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated;
        switch (uriMatcher.match(uri)) {
            case TRUCK_STATE:
                rowsUpdated = db.update(TruckStateEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRUCK_ALL:

                throw new RuntimeException();
                // TODO Migrate to using selection and args. Just here for backwards compatibility
//                QueryArgs args = new QueryArgs(uri);
//                rowsUpdated = db.update(TruckCombo.VIEW_NAME, values, args.selection, args.selectionArgs);
//                break;
            case TRUCK_SINGLE:

                throw new RuntimeException();
                // TODO Migrate to using selection and args. Just here for backwards compatibility
//                selection = TruckEntry.COLUMN_INTERNAL_ID + "=?";
//                selectionArgs = new String[]{TruckCombo.getInternalIdFromUri(uri)};
//                rowsUpdated = db.update(TruckCombo.VIEW_NAME, values, selection, selectionArgs);
//                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Timber.d("Suppressing notify: %b", suppressNotify);
        if (rowsUpdated != 0 && !suppressNotify) {
            getContext().getContentResolver().notifyChange(uri, null, needsSync);
        }
        return rowsUpdated;
    }

    @DebugLog
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
                uri = TruckCombo.CONTENT_URI;
                break;
            case TRUCK_STATE:
                returnCount = TruckStateTable.bulkInsert(db, values);
                uri = TruckCombo.CONTENT_URI;
                break;
            default:
                Timber.w("Attempting a bulk insert for an unsupported URI, %s. Falling back to normal inserts...", uri);
                returnCount = super.bulkInsert(uri, values);
        }
        if (returnCount > 0 && !suppressNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null, needsSync(uri));
        }
        return returnCount;
    }
}