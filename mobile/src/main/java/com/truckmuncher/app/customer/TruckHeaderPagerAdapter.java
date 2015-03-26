package com.truckmuncher.app.customer;

import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.truckmuncher.app.data.PublicContract;

import java.util.ArrayList;

public class TruckHeaderPagerAdapter extends FragmentStatePagerAdapter {

    private final Cursor cursor;

    public TruckHeaderPagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        this.cursor = cursor;
    }

    @Override
    public TruckHeaderFragment getItem(int i) {
        cursor.moveToPosition(i);
        return TruckHeaderFragment.newInstance(cursor.getString(Query.TRUCK_ID));
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    public String getTruckId(int position) {
        cursor.moveToPosition(position);
        return cursor.getString(Query.TRUCK_ID);
    }

    public int getTruckPosition(String truckId) {
        for (int i = 0; i < getCount(); i++) {
            cursor.moveToPosition(i);

            if (cursor.getString(Query.TRUCK_ID).equals(truckId)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<String> getTruckIds() {
        ArrayList<String> truckIds = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        do {
            truckIds.add(cursor.getString(Query.TRUCK_ID));
        } while (cursor.moveToNext());
        return truckIds;
    }

    public interface Query {
        static final String[] PROJECTION = new String[]{PublicContract.Truck.ID};
        static final int TRUCK_ID = 0;
    }
}
