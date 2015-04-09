package com.truckmuncher.app.customer;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.truckmuncher.api.trucks.Truck;
import com.truckmuncher.app.R;
import com.truckmuncher.app.data.PublicContract;

import java.util.ArrayList;

public class TrucksGridAdapter extends SimpleCursorAdapter {


    public TrucksGridAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c, TruckQuery.PROJECTION, new int[]{R.id.truck_name, R.id.truck_image}, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Truck truck = new Truck.Builder()
                .name(cursor.getString(TruckQuery.NAME))
                .imageUrl(cursor.getString(TruckQuery.IMAGE_URL))
                .build();

        TextView textView = (TextView) view.findViewById(R.id.truck_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.truck_image);

        textView.setText(truck.name);

        Picasso.with(context)
                .load(truck.imageUrl)
                .fit()
                .centerInside()
                .transform(new CircleTransform())
                .into(imageView);
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
                PublicContract.Truck.IMAGE_URL,
                PublicContract.Truck.KEYWORDS,
                PublicContract.Truck.COLOR_SECONDARY
        };
        int _ID = 0;
        int ID = 1;
        int NAME = 2;
        int IMAGE_URL = 3;
        int KEYWORDS = 4;
        int COLOR_SECONDARY = 5;
    }
}
