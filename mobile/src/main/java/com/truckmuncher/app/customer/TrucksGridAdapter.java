package com.truckmuncher.app.customer;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.truckmuncher.app.R;
import com.truckmuncher.app.data.PublicContract;

import java.util.ArrayList;

public class TrucksGridAdapter extends SimpleCursorAdapter {

    public TrucksGridAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c, TruckQuery.PROJECTION, new int[]{R.id.truck_name, R.id.truck_image}, 0);
    }

    @Override
    public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor) {
        String name = cursor.getString(TruckQuery.NAME);
        String imageUrl = cursor.getString(TruckQuery.IMAGE_URL);

        TextView textView = (TextView) view.findViewById(R.id.truck_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.truck_image);

        textView.setText(name);

        Picasso.with(context)
                .load(imageUrl)
                .fit()
                .centerInside()
                .transform(new CircleTransform())
                .into(imageView);
    }

    public int getTruckPosition(String truckId) {
        Cursor cursor = getCursor();
        for (int i = 0; i < getCount(); i++) {
            cursor.moveToPosition(i);

            if (cursor.getString(TruckQuery.ID).equals(truckId)) {
                return i;
            }
        }
        return -1;
    }

    public String getTruckId(int position) {
        getCursor().moveToPosition(position);
        return getCursor().getString(TruckQuery.ID);
    }

    public ArrayList<String> getTruckIds() {
        ArrayList<String> truckIds = new ArrayList<>(getCursor().getCount());
        getCursor().moveToFirst();
        do {
            truckIds.add(getCursor().getString(TruckQuery.ID));
        } while (getCursor().moveToNext());
        return truckIds;
    }

    interface TruckQuery {
        String[] PROJECTION = new String[]{
                PublicContract.Truck._ID,
                PublicContract.Truck.ID,
                PublicContract.Truck.NAME,
                PublicContract.Truck.IMAGE_URL
        };
        int _ID = 0;
        int ID = 1;
        int NAME = 2;
        int IMAGE_URL = 3;
    }
}
