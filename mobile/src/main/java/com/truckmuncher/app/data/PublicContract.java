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
        String _ID = BaseColumns._ID;
        String ID = "id";
        String NAME = "name";
        String NOTES = "notes";
        String ORDER_IN_MENU = "order_in_menu";
        String TRUCK_ID = "truck_id";
    }

    public interface MenuItem {
        String _ID = BaseColumns._ID;
        String ID = "id";
        String NAME = "name";
        String PRICE = "price";
        String IS_AVAILABLE = "is_available";
        String NOTES = "notes";
        String TAGS = "tags";
        String ORDER_IN_CATEGORY = "order_in_category";
        String CATEGORY_ID = "category_id";
    }

    public interface Truck {
        /**
         * Do <b>NOT</b> use this for uniquely identifying trucks. This value is only useful for
         * use in adapters and the like.
         * <p/>
         * Type: long
         */
        String _ID = BaseColumns._ID;
        /**
         * Use this for uniquely identifying trucks.
         * <p/>
         * Type: String
         */
        String ID = "id";
        /**
         * Type: String
         */
        String NAME = "name";
        /**
         * Type: String
         */
        String IMAGE_URL = "image_url";
        /**
         * List of keywords, stored as a list
         * <p/>
         * Type: String
         *
         * @see com.truckmuncher.app.data.PublicContract#convertStringToList(String)
         */
        String KEYWORDS = "keywords";
        /**
         * Type: String
         */
        String COLOR_PRIMARY = "color_primary";
        /**
         * Type: String
         */
        String COLOR_SECONDARY = "color_secondary";
        /**
         * Type: boolean
         */
        String IS_SERVING = "is_serving";
        /**
         * Type: boolean
         */
        // TODO we should see if there is a more transient way for us to do this
        String MATCHED_SEARCH = "matched_search";
        /**
         * Type: double
         */
        String LATITUDE = "latitude";
        /**
         * Type: double
         */
        String LONGITUDE = "longitude";
        /**
         * Type: boolean
         */
        // TODO this mechanism is becoming really sketch now that we are getting user support for non-trucks. Should see if there is a better way of doing this.
        String OWNED_BY_CURRENT_USER = "owned_by_current_user";
        /**
         * Type: String
         */
        String DESCRIPTION = "description";
        /**
         * Formatted as (xxx) xxx-xxxx
         * <p/>
         * Type: String
         */
        String PHONE_NUMBER = "phone_number";
        /**
         * Type: String
         */
        String WEBSITE = "website";

        String IS_FAVORITE = "is_favorite";
    }

    public interface Menu {
        String _ID = BaseColumns._ID;
        String MENU_ITEM_ID = "menu_item_id";
        String MENU_ITEM_NAME = "menu_item_name";
        String PRICE = "price";
        String MENU_ITEM_NOTES = "menu_item_notes";
        String ORDER_IN_CATEGORY = "order_in_category";
        String IS_AVAILABLE = "is_available";
        String CATEGORY_NAME = "category_name";
        String CATEGORY_ID = "category_id";
        String CATEGORY_NOTES = "category_notes";
        String ORDER_IN_MENU = "order_in_menu";
        String TRUCK_ID = "truck_id";
        String MENU_ITEM_TAGS = "menu_item_tags";
    }
}
