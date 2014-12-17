package com.truckmuncher.truckmuncher.customer;

import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import static com.truckmuncher.truckmuncher.data.Contract.TruckCombo;

public class CursorFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private final Cursor cursor;

    public CursorFragmentStatePagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        this.cursor = cursor;
    }

    @Override
    public CustomerMenuFragment getItem(int i) {
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

    public String getTruckId(int position) {
        cursor.moveToPosition(position);
        return cursor.getString(Query.TRUCK_ID);
    }

    public interface Query {
        static final String[] PROJECTION = new String[]{
                TruckCombo.COLUMN_INTERNAL_ID,
                TruckCombo.COLUMN_NAME,
                TruckCombo.COLUMN_IMAGE_URL,
                TruckCombo.COLUMN_KEYWORDS,
                TruckCombo.COLUMN_COLOR_PRIMARY
        };

        static final int TRUCK_ID = 0;
        static final int NAME = 1;
        static final int IMAGE_URL = 2;
        static final int KEYWORDS = 3;
        static final int PRIMARY_COLOR = 4;
    }
}
