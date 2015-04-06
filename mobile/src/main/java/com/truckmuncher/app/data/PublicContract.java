package com.truckmuncher.app.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.truckmuncher.app.BuildConfig;

import java.util.Arrays;
import java.util.List;

public class PublicContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final Uri CATEGORY_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/category");
    public static final Uri MENU_ITEM_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/menu_item");
    public static final Uri TRUCK_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/truck");
    public static final Uri MENU_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/menu");
    public static final String URI_TYPE_CATEGORY = "vnd.android.cursor.dir/vnd.truckmuncher.category";
    public static final String URI_TYPE_MENU_ITEM = "vnd.android.cursor.dir/vnd.truckmuncher.menu_item";
    public static final String URI_TYPE_TRUCK = "vnd.android.cursor.dir/vnd.truckmuncher.truck";
    public static final String URI_TYPE_MENU = "vnd.android.cursor.dir/vnd.truckmuncher.menu";
    private static final String STRING_SEPARATOR = ",";

    /**
     * Used to store a list representation as a single string
     *
     * @see #convertStringToList(String)
     */
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

    /**
     * Used to parse a lis that has been stored as a single string
     *
     * @see #convertListToString(java.util.List)
     */
    public static List<String> convertStringToList(String string) {
        return Arrays.asList(string.split(STRING_SEPARATOR));
    }

    public interface Category {
        public static final String _ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String NOTES = "notes";
        public static final String ORDER_IN_MENU = "order_in_menu";
        public static final String TRUCK_ID = "truck_id";
    }

    public interface MenuItem {
        public static final String _ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String IS_AVAILABLE = "is_available";
        public static final String NOTES = "notes";
        public static final String TAGS = "tags";
        public static final String ORDER_IN_CATEGORY = "order_in_category";
        public static final String CATEGORY_ID = "category_id";
    }

    public interface Truck {
        /**
         * Do <b>NOT</b> use this for uniquely identifying trucks. This value is only useful for
         * use in adapters and the like.
         * <p/>
         * Type: long
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Use this for uniquely identifying trucks.
         * <p/>
         * Type: String
         */
        public static final String ID = "id";
        /**
         * Type: String
         */
        public static final String NAME = "name";
        /**
         * Type: String
         */
        public static final String IMAGE_URL = "image_url";
        /**
         * List of keywords, stored as a list
         * <p/>
         * Type: String
         *
         * @see com.truckmuncher.app.data.PublicContract#convertStringToList(String)
         */
        public static final String KEYWORDS = "keywords";
        /**
         * Type: String
         */
        public static final String COLOR_PRIMARY = "color_primary";
        /**
         * Type: String
         */
        public static final String COLOR_SECONDARY = "color_secondary";
        /**
         * Type: boolean
         */
        public static final String IS_SERVING = "is_serving";
        /**
         * Type: boolean
         */
        // TODO we should see if there is a more transient way for us to do this
        public static final String MATCHED_SEARCH = "matched_search";
        /**
         * Type: double
         */
        public static final String LATITUDE = "latitude";
        /**
         * Type: double
         */
        public static final String LONGITUDE = "longitude";
        /**
         * Type: boolean
         */
        // TODO this mechanism is becoming really sketch now that we are getting user support for non-trucks. Should see if there is a better way of doing this.
        public static final String OWNED_BY_CURRENT_USER = "owned_by_current_user";
        /**
         * Type: String
         */
        public static final String DESCRIPTION = "description";
        /**
         * Formatted as (xxx) xxx-xxxx
         * <p/>
         * Type: String
         */
        public static final String PHONE_NUMBER = "phone_number";
        /**
         * Type: String
         */
        public static final String WEBSITE = "website";
    }

    public interface Menu {
        public static final String _ID = BaseColumns._ID;
        public static final String MENU_ITEM_ID = "menu_item_id";
        public static final String MENU_ITEM_NAME = "menu_item_name";
        public static final String PRICE = "price";
        public static final String MENU_ITEM_NOTES = "menu_item_notes";
        public static final String ORDER_IN_CATEGORY = "order_in_category";
        public static final String IS_AVAILABLE = "is_available";
        public static final String CATEGORY_NAME = "category_name";
        public static final String CATEGORY_ID = "category_id";
        public static final String CATEGORY_NOTES = "category_notes";
        public static final String ORDER_IN_MENU = "order_in_menu";
        public static final String TRUCK_ID = "truck_id";
        public static final String MENU_ITEM_TAGS = "menu_item_tags";
    }
}
