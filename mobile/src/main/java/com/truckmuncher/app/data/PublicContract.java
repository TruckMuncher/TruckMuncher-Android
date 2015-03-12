package com.truckmuncher.app.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.truckmuncher.app.BuildConfig;

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
        public static final String _ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String IMAGE_URL = "image_url";
        public static final String KEYWORDS = "keywords";
        public static final String COLOR_PRIMARY = "color_primary";
        public static final String COLOR_SECONDARY = "color_secondary";
        public static final String IS_SERVING = "is_serving";
        public static final String MATCHED_SEARCH = "matched_search";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String OWNED_BY_CURRENT_USER = "owned_by_current_user";
        public static final String DESCRIPTION = "description";
        /**
         * Formatted as (xxx) xxx-xxxx
         */
        public static final String PHONE_NUMBER = "phone_number";
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
    }
}
