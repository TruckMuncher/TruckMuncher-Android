package com.truckmuncher.truckmuncher.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.truckmuncher.truckmuncher.BuildConfig;

import java.util.Arrays;
import java.util.List;

public class Contract {

    public static final String CONTENT_AUTHORITY = BuildConfig.PACKAGE_NAME;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content__//" + CONTENT_AUTHORITY);
    protected static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/";
    protected static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/";

    public static final String PATH_TRUCK = "truck";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_MENU_ITEM = "menu_item";
    public static final String PATH_MENU = "menu";

    private static final String STRING_SEPARATOR = "__,__";

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

    public static final class TruckEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRUCK).build();
        public static final String TABLE_NAME = "truck";
        public static final String COLUMN_INTERNAL_ID = TABLE_NAME + "__internal_id";
        public static final String COLUMN_NAME = TABLE_NAME + "__name";
        public static final String COLUMN_IMAGE_URL = TABLE_NAME + "__image_url";
        public static final String COLUMN_KEYWORDS = TABLE_NAME + "__keywords";

        public static final String CONTENT_TYPE = CONTENT_TYPE_BASE + PATH_TRUCK;

        public static final String CONTENT_ITEM_TYPE = CONTENT_ITEM_TYPE_BASE + PATH_TRUCK;
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

        public static final String CONTENT_ITEM_TYPE = CONTENT_ITEM_TYPE_BASE + PATH_CATEGORY;
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

        public static final String CONTENT_TYPE = CONTENT_TYPE_BASE + PATH_MENU_ITEM;

        public static final String CONTENT_ITEM_TYPE = CONTENT_ITEM_TYPE_BASE + PATH_MENU_ITEM;
    }

    public static final class MenuEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MENU).build();
        public static final String VIEW_NAME = "view_menu";

        public static Uri buildMenuForTruck(String truckId) {
            return CONTENT_URI.buildUpon().appendQueryParameter(TruckEntry.COLUMN_INTERNAL_ID, truckId).build();
        }

        public static final String CONTENT_TYPE = CONTENT_TYPE_BASE + PATH_MENU;

        public static final String CONTENT_ITEM_TYPE = CONTENT_ITEM_TYPE_BASE + PATH_MENU;
    }
}
