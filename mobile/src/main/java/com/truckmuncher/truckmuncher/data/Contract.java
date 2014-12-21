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
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    protected static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/";
    protected static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/";

    public static final String PATH_CATEGORY = "category";
    public static final String PATH_MENU_ITEM = "menu_item";
    public static final String PATH_MENU = "menu";

    private static final String STRING_SEPARATOR = ",";
    private static final String PARAM_NEEDS_SYNC = "needs_sync";
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

    public static Uri syncToNetwork(Uri uri) {
        return uri.buildUpon().appendQueryParameter(PARAM_SYNC_TO_NETWORK, "true").build();
    }

    public static boolean isSyncToNetwork(Uri uri) {
        String sync = uri.getQueryParameter(PARAM_SYNC_TO_NETWORK);
        return sync != null && Boolean.parseBoolean(sync);
    }

    public static Uri syncFromNetwork(Uri uri) {
        return uri.buildUpon().appendQueryParameter(PARAM_SYNC_FROM_NETWORK, "true").build();
    }

    public static boolean isSyncFromNetwork(Uri uri) {
        String sync = uri.getQueryParameter(PARAM_SYNC_FROM_NETWORK);
        return sync != null && Boolean.parseBoolean(sync);
    }

    /**
     * If used, the resulting Uri will have it's data synced to the network.
     *
     * @param uri to sync
     */
    public static Uri buildNeedsSync(Uri uri) {
        return uri.buildUpon().appendQueryParameter(PARAM_NEEDS_SYNC, "true").build();
    }

    public static boolean needsSync(Uri uri) {
        String needsSync = uri.getQueryParameter(PARAM_NEEDS_SYNC);
        return needsSync != null && Boolean.parseBoolean(needsSync);
    }

    /**
     * If used, the resulting Uri will not have it's listeners notified when new data is available.
     */
    public static Uri suppressNotify(Uri uri) {
        return uri.buildUpon().appendQueryParameter(PARAM_NOTIFY, "false").build();
    }

    public static boolean isSuppressNotify(Uri uri) {
        String suppress = uri.getQueryParameter(PARAM_NOTIFY);
        return suppress != null && !Boolean.parseBoolean(suppress);
    }

    /**
     * Stores the temporary state. You must use this to do writes to the db, but should not use this for queries.
     */
    public interface TruckStateEntry extends BaseColumns {

        public static final String TABLE_NAME = "truck_state";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String COLUMN_INTERNAL_ID = TABLE_NAME + "__internal_id";
        public static final String COLUMN_IS_SELECTED_TRUCK = TABLE_NAME + "__is_selected";
        public static final String COLUMN_IS_SERVING = TABLE_NAME + "__is_serving";
        public static final String COLUMN_LATITUDE = TABLE_NAME + "__latitude";
        public static final String COLUMN_LONGITUDE = TABLE_NAME + "__longitude";
        public static final String COLUMN_IS_DIRTY = TABLE_NAME + "__is_dirty";
    }

    /**
     * Stores the permanent state. You must use this to do writes to the db, but should not use this for queries.
     */
    public interface TruckConstantEntry extends BaseColumns {

        public static final String TABLE_NAME = "truck";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_INTERNAL_ID = TABLE_NAME + "__internal_id";
        public static final String COLUMN_NAME = TABLE_NAME + "__name";
        public static final String COLUMN_IMAGE_URL = TABLE_NAME + "__image_url";
        public static final String COLUMN_KEYWORDS = TABLE_NAME + "__keywords";
        public static final String COLUMN_COLOR_PRIMARY = TABLE_NAME + "__color_primary";
        public static final String COLUMN_COLOR_SECONDARY = TABLE_NAME + "__color_secondary";
        public static final String COLUMN_OWNED_BY_CURRENT_USER = TABLE_NAME + "__owned_by_current_user";
    }

    /**
     * Gives a holistic view of a truck. Use this for queries but not writes.
     */
    public static final class TruckEntry implements TruckConstantEntry, TruckStateEntry {

        public static final String VIEW_NAME = "truck_view";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(VIEW_NAME).build();
        public static final String COLUMN_INTERNAL_ID = TruckConstantEntry.TABLE_NAME + "__internal_id";

        public static Query buildSingleTruck(String internalId) {
            return new Query.Builder()
                    .where(TruckStateEntry.COLUMN_INTERNAL_ID, EQUALS, internalId)
                    .build();
        }

        public static Query buildServingTrucks() {
            return new Query.Builder()
                    .where(COLUMN_IS_SERVING, EQUALS, true)
                    .build();
        }

        public static Query buildDirty() {
            return new Query.Builder()
                    .where(COLUMN_IS_DIRTY, EQUALS, true)
                    .build();
        }

        public static final String CONTENT_TYPE = CONTENT_TYPE_BASE + VIEW_NAME;

        public static final String CONTENT_ITEM_TYPE = CONTENT_ITEM_TYPE_BASE + VIEW_NAME;
    }

    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_INTERNAL_ID = TABLE_NAME + "__internal_id";
        public static final String COLUMN_NAME = TABLE_NAME + "__name";
        public static final String COLUMN_NOTES = TABLE_NAME + "__notes";
        public static final String COLUMN_ORDER_IN_MENU = TABLE_NAME + "__order_in_menu";
        public static final String COLUMN_TRUCK_ID = TABLE_NAME + "__truck_id";

        public static final String CONTENT_TYPE = CONTENT_TYPE_BASE + PATH_CATEGORY;
    }

    public static final class MenuItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MENU_ITEM).build();
        public static final String TABLE_NAME = "menu_item";
        public static final String COLUMN_INTERNAL_ID = TABLE_NAME + "__internal_id";
        public static final String COLUMN_NAME = TABLE_NAME + "__name";
        public static final String COLUMN_PRICE = TABLE_NAME + "__price";
        public static final String COLUMN_IS_AVAILABLE = TABLE_NAME + "__is_available";
        public static final String COLUMN_NOTES = TABLE_NAME + "__notes";
        public static final String COLUMN_TAGS = TABLE_NAME + "__tags";
        public static final String COLUMN_ORDER_IN_CATEGORY = TABLE_NAME + "__order_in_category";
        public static final String COLUMN_CATEGORY_ID = TABLE_NAME + "__category_id";
        public static final String COLUMN_IS_DIRTY = TABLE_NAME + "__is_dirty";

        public static Uri buildDirty() {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_IS_DIRTY, "1").build();
        }

        public static final String CONTENT_TYPE = CONTENT_TYPE_BASE + PATH_MENU_ITEM;
    }

    public static final class MenuEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MENU).build();
        public static final String VIEW_NAME = "view_menu";

        public static Query buildMenuForTruck(String truckId) {
            return new Query.Builder()
                    .where(TruckConstantEntry.COLUMN_INTERNAL_ID, EQUALS, truckId)
                    .build();
        }
    }
}
