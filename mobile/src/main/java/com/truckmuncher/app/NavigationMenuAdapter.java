package com.truckmuncher.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationMenuAdapter extends BaseAdapter {

    public static final long ITEM_LIVE_MAP = 0;
    public static final long ITEM_ALL_TRUCKS = 1;
    public static final long ITEM_LOGOUT = 10;
    public static final long ITEM_MY_TRUCKS = 20;
    public static final long ITEM_LOGIN = 30;

    private static int[] NAV_COMMON = new int[]{
            R.string.nav_common_live_map,
            R.string.nav_common_all_trucks
    };

    private static int[] NAV_AUTH = new int[]{
            R.string.nav_auth_log_out
    };

    private static int[] NAV_VENDOR = new int[]{
            R.string.nav_vendor_my_trucks
    };

    private static int[] NAV_UNAUTH = new int[]{
            R.string.nav_unauth_log_in
    };

    private static final Map<Integer, Long> ID_MAP;
    static {
        Map<Integer, Long> map = new HashMap<>();
        map.put(R.string.nav_common_live_map, ITEM_LIVE_MAP);
        map.put(R.string.nav_common_all_trucks, ITEM_ALL_TRUCKS);
        map.put(R.string.nav_auth_log_out, ITEM_LOGOUT);
        map.put(R.string.nav_vendor_my_trucks, ITEM_MY_TRUCKS);
        map.put(R.string.nav_unauth_log_in, ITEM_LOGIN);
        ID_MAP = Collections.unmodifiableMap(map);
    }

    private Context context;
    private boolean isLoggedIn;
    private boolean isVendor;
    private List<Integer> currentItems;

    public NavigationMenuAdapter(Context context, boolean isLoggedIn, boolean isVendor) {
        super();
        this.context = context;
        this.isLoggedIn = isLoggedIn;
        this.isVendor = isVendor;
        this.currentItems = new ArrayList<>();

        updateItems();
    }

    @Override
    public int getCount() {
        return currentItems.size();
    }

    @Override
    public Object getItem(int i) {
        return context.getString(currentItems.get(i));
    }

    @Override
    public long getItemId(int i) {
        return ID_MAP.get(currentItems.get(i));
    }

    public int getItemPosition(long id) {
        return currentItems.indexOf(id);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }

        TextView text = (TextView) view.findViewById(android.R.id.text1);

        text.setText(context.getString(currentItems.get(i)));

        return view;
    }

    private void updateItems() {
        currentItems.clear();

        addItems(NAV_COMMON);

        if (isLoggedIn) {
            if (isVendor) {
                addItems(NAV_VENDOR);
            }

            addItems(NAV_AUTH);
        }
        else {
            addItems(NAV_UNAUTH);
        }
    }

    private void addItems(int[] stringIds) {
        for (int id : stringIds) {
            currentItems.add(id);
        }
    }
}
