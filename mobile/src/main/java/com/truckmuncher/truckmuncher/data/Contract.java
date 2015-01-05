package com.truckmuncher.truckmuncher.data;

import android.net.Uri;

import java.util.Arrays;
import java.util.List;

public final class Contract {

    public static final Uri TRUCK_STATE_URI = Uri.parse("content://" + PublicContract.CONTENT_AUTHORITY + "/truck_state");
    public static final Uri TRUCK_PROPERTIES_URI = Uri.parse("content://" + PublicContract.CONTENT_AUTHORITY + "/truck_properties");
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
        return uri.buildUpon().appendQueryParameter(PARAM_NOTIFY, "true").build();
    }

    public static boolean isSuppressNotify(Uri uri) {
        return uri.getBooleanQueryParameter(PARAM_NOTIFY, false);
    }

    /**
     * Stores the temporary state. You must use this to do writes to the db, but should not use this for queries.
     */
    public interface TruckState {
        public static final String IS_DIRTY = "is_dirty";
    }

    public interface MenuItem {
        public static final String IS_DIRTY = "is_dirty";
    }
}
