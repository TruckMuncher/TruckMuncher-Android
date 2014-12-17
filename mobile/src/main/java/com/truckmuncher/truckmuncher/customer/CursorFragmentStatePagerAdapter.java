package com.truckmuncher.truckmuncher.customer;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import static com.truckmuncher.truckmuncher.data.Contract.TruckEntry;

public class CursorFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private final Cursor cursor;

    public CursorFragmentStatePagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        this.cursor = cursor;
    }

    @Override
    public Fragment getItem(int i) {
        cursor.moveToPosition(i);
        return CustomerMenuFragment.newInstance(
                cursor.getString(Query.TRUCK_ID),
                cursor.getString(Query.NAME),
                cursor.getString(Query.IMAGE_URL),
                cursor.getString(Query.KEYWORDS),
                cursor.getString(Query.PRIMARY_COLOR)
        );
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    public interface Query {
        static final String[] PROJECTION = new String[]{
                TruckEntry.COLUMN_INTERNAL_ID,
                TruckEntry.COLUMN_NAME,
                TruckEntry.COLUMN_IMAGE_URL,
                TruckEntry.COLUMN_KEYWORDS,
                TruckEntry.COLUMN_COLOR_PRIMARY
        };

        static final int TRUCK_ID = 0;
        static final int NAME = 1;
        static final int IMAGE_URL = 2;
        static final int KEYWORDS = 3;
        static final int PRIMARY_COLOR = 4;
    }
}
