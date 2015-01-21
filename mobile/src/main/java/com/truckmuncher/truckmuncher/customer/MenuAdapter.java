package com.truckmuncher.truckmuncher.customer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.PublicContract;
import com.twotoasters.sectioncursoradapter.SectionCursorAdapter;
import com.volkhart.androidutil.text.Truss;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @see <a href="https://github.com/twotoasters/SectionCursorAdapter">GitHub Project</a>
 */
public class MenuAdapter extends SectionCursorAdapter {

    private final String categoryColorHex;

    public MenuAdapter(Context context, String categoryColorHex) {
        super(context, null, 0);
        this.categoryColorHex = categoryColorHex;
    }

    @Override
    protected Object getSectionFromCursor(Cursor cursor) {
        return cursor.getString(Query.CATEGORY_NAME);
    }

    @Override
    protected View newSectionView(Context context, Object o, ViewGroup viewGroup) {
        TextView view = (TextView) getLayoutInflater().inflate(R.layout.list_item_menu_category, viewGroup, false);
        if (categoryColorHex != null) {
            view.setBackgroundColor(Color.parseColor(categoryColorHex));
            view.setTextColor(ColorCorrector.calculateTextColor(categoryColorHex));
        }
        return view;
    }

    @Override
    protected void bindSectionView(View view, Context context, int i, Object o) {
        ButterKnife.<TextView>findById(view, android.R.id.text1).setText((String) o);
    }

    @Override
    protected View newItemView(Context context, Cursor cursor, ViewGroup parent) {
        View view = getLayoutInflater().inflate(R.layout.list_item_menu_item, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    protected void bindItemView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // Name
        CharSequence name = cursor.getString(Query.NAME);
        boolean isAvailable = cursor.getInt(Query.IS_AVAILABLE) == 1;
        if (!isAvailable) {
            name = new Truss()
                    .pushSpan(new StrikethroughSpan())
                    .append(name)
                    .build();
        }
        holder.name.setText(name);

        // Price
        String price = NumberFormat.getCurrencyInstance(Locale.US).format(cursor.getDouble(Query.PRICE));
        holder.price.setText(price);
    }

    interface Query {

        static final String[] PROJECTION = new String[]{
                PublicContract.Menu._ID,
                PublicContract.Menu.MENU_ITEM_ID,
                PublicContract.Menu.MENU_ITEM_NAME,
                PublicContract.Menu.PRICE,
                PublicContract.Menu.IS_AVAILABLE,
                PublicContract.Menu.CATEGORY_NAME
        };
        static final int ID = 1;
        static final int NAME = 2;
        static final int PRICE = 3;
        static final int IS_AVAILABLE = 4;
        static final int CATEGORY_NAME = 5;
    }

    static class ViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.price)
        TextView price;

        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
