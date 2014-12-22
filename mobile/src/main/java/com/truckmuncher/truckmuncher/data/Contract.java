package com.truckmuncher.truckmuncher.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.truckmuncher.truckmuncher.BuildConfig;
import com.truckmuncher.truckmuncher.data.sql.Query;

import java.util.Arrays;
import java.util.List;

import static com.truckmuncher.truckmuncher.data.sql.Query.Operator.EQUALS;

public final class Contract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/provider");
    private static final String CONTENT_TYPE_DIR_BASE = "vnd.android.cursor.dir/vnd.truckmuncher.";
    private static final String CONTENT_TYPE_ITEM_BASE = "vnd.android.cursor.item/vnd.truckmuncher.";
    private static final String STRING_SEPARATOR = ",";
    private static final String PARAM_NOTIFY = "notify";
    private static final String PARAM_SYNC_TO_NETWORK = "sync_to_network";
    private static final String PARAM_SYNC_FROM_NETWORK = "sync_from_network";

    private Contract() {
        // No instances
    }

    public static String convertListToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, max = list.size(); i < max; i++) {
            builder.append(list.get(i));
            if (i < list.size() - 1) {
                builder.append(STRING_SEPARATOR);
            }
        }
        return builder.toString();
    }

    public static List<String> convertStringToList(String string) {
        return Arrays.asList(string.split(STRING_SEPARATOR));
    }

    /**
     * If used, the ContentProvider will attempt to sync the provided uri to the network. This is
     * incompatible with the {@link #suppressNotify(android.net.Uri)} directive.
     *
     * @param uri to sync
     * @return original uri with the directive attached
     */
    public static Uri syncToNetwork(Uri uri) {
        if (isSuppressNotify(uri)) {
            throw new IllegalStateException("The syncToNetwork directive cannot be used with the suppressNotify directive.");
        }
        return uri.buildUpon().appendQueryParameter(PARAM_SYNC_TO_NETWORK, "true").build();
    }

    public static boolean isSyncToNetwork(Uri uri) {
        return uri.getBooleanQueryParameter(PARAM_SYNC_TO_NETWORK, false);
    }

    /**
     * If used, the ContentProvider will attempt to sync the provided uri from the network
     *
     * @param uri to sync
     * @return original uri with the directive attached
     */
    public static Uri syncFromNetwork(Uri uri) {
        return uri.buildUpon().appendQueryParameter(PARAM_SYNC_FROM_NETWORK, "true").build();
    }

    public static boolean isSyncFromNetwork(Uri uri) {
        return uri.getBooleanQueryParameter(PARAM_SYNC_FROM_NETWORK, false);
    }

    /**
     * If used, the resulting Uri will not have it's listeners notified when new data is available.
     * This is incompatible with the {@link #syncToNetwork(android.net.Uri)} directive.
     *
     * @param uri to sync
     * @return original uri with the directive attached
     */
    public static Uri suppressNotify(Uri uri) {
        if (isSyncToNetwork(uri)) {
            throw new IllegalStateException("The suppressNotify directive cannot be used with the syncToNetwork directive.");
        }
        return uri.buildUpon().appendQueryParameter(PARAM_NOTIFY, "false").build();
    }

    public static boolean isSuppressNotify(Uri uri) {
        return uri.getBooleanQueryParameter(PARAM_NOTIFY, false);
    }

    /**
     * Stores the temporary state. You must use this to do writes to the db, but should not use this for queries.
     */
    public interface TruckState extends PublicContract.TruckState {
        /**
         * Unused
         */
        public static final String IS_SELECTED_TRUCK = "is_selected";
        public static final String IS_DIRTY = "is_dirty";
    }

    /**
     * Stores the permanent state. You must use this to do writes to the db, but should not use this for queries.
     */
    public interface TruckConstantEntry {

        public static final String TABLE_NAME = "truck_constant";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_INTERNAL_ID = TABLE_NAME + "__internal_id";
        public static final String COLUMN_NAME = TABLE_NAME + "__name";
        public static final String COLUMN_IMAGE_URL = TABLE_NAME + "__image_url";
        public static final String COLUMN_KEYWORDS = TABLE_NAME + "__keywords";
        public static final String COLUMN_COLOR_PRIMARY = TABLE_NAME + "__color_primary";
        public static final String COLUMN_COLOR_SECONDARY = TABLE_NAME + "__color_secondary";
        public static final String COLUMN_OWNED_BY_CURRENT_USER = TABLE_NAME + "__owned_by_current_user";
    }

    public interface Category extends PublicContract.Category {
        public static final String ORDER_IN_MENU = "order_in_menu";
        public static final String TRUCK_ID = "truck_id";
    }

    public interface MenuItem extends PublicContract.MenuItem {
        public static final String ORDER_IN_CATEGORY = "order_in_category";
        public static final String CATEGORY_ID = "category_id";
        public static final String IS_DIRTY = "is_dirty";
    }

    /**
     * Gives a holistic view of a truck. Use this for queries but not writes.
     */
    public static final class TruckEntry implements TruckConstantEntry, TruckState {

        public static final String VIEW_NAME = "truck";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(VIEW_NAME).build();
        public static final String COLUMN_INTERNAL_ID = TruckConstantEntry.TABLE_NAME + "__internal_id";

        public static Query buildSingleTruck(String internalId) {
            return new Query.Builder()
                    .where(TruckState.ID, EQUALS, internalId)
                    .build();
        }

        public static Query buildServingTrucks() {
            return new Query.Builder()
                    .where(IS_SERVING, EQUALS, true)
                    .build();
        }

        public static Query buildDirty() {
            return new Query.Builder()
                    .where(IS_DIRTY, EQUALS, true)
                    .build();
        }

        public static final String CONTENT_TYPE = CONTENT_TYPE_DIR_BASE + VIEW_NAME;

        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_BASE + VIEW_NAME;
    }

    public static final class MenuEntry implements BaseColumns {

        public static final String VIEW_NAME = "view_menu";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(VIEW_NAME).build();

        public static Query buildMenuForTruck(String truckId) {
            return new Query.Builder()
                    .where(TruckConstantEntry.COLUMN_INTERNAL_ID, EQUALS, truckId)
                    .build();
        }
    }
}
