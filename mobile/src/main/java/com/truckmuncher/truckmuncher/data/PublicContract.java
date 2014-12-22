package com.truckmuncher.truckmuncher.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.truckmuncher.truckmuncher.BuildConfig;

public class PublicContract {

    public static final Uri CATEGORY_URI = Uri.parse("content://" + BuildConfig.APPLICATION_ID + ".provider/category");
    public static final Uri MENU_ITEM_URI = Uri.parse("content://" + BuildConfig.APPLICATION_ID + ".provider/menu_item");
    public static final Uri TRUCK_STATE_URI = Uri.parse("content://" + BuildConfig.APPLICATION_ID + ".provider/truck_state");
    public static final String URI_TYPE_CATEGORY = "vnd.android.cursor.dir/vnd.truckmuncher.category";
    public static final String URI_TYPE_MENU_ITEM = "vnd.android.cursor.dir/vnd.truckmuncher.menu_item";
    public static final String URI_TYPE_TRUCK_STATE = "vnd.android.cursor.dir/vnd.truckmuncher.truck_state";

    public interface Category {
        public static final String _ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String NOTES = "notes";
    }

    public interface MenuItem {
        public static final String _ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String IS_AVAILABLE = "is_available";
        public static final String NOTES = "notes";
        public static final String TAGS = "tags";
    }

    public interface TruckState {
        public static final String _ID = BaseColumns._ID;
        public static final String ID = "id";
        public static final String IS_SERVING = "is_serving";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }

    public interface TruckProperties {

    }

    public interface Truck {

    }
}
