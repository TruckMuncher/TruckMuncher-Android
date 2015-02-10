package com.truckmuncher.app.customer;

import android.content.Context;
import android.database.Cursor;
import android.text.style.StrikethroughSpan;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truckmuncher.app.R;
import com.truckmuncher.app.data.PublicContract;
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

    private final int textColor;

    public MenuAdapter(Context context, int textColor) {
        super(context, null, 0);
        this.textColor = textColor;
    }

    @Override
    protected Object getSectionFromCursor(Cursor cursor) {
        return new Pair<>(cursor.getString(Query.CATEGORY_NAME), cursor.getString(Query.CATEGORY_NOTES));
    }

    @Override
    protected View newSectionView(Context context, Object o, ViewGroup viewGroup) {
        View view = getLayoutInflater().inflate(R.layout.list_item_menu_category, viewGroup, false);
        CategoryViewHolder holder = new CategoryViewHolder(view);
        holder.name.setTextColor(textColor);
        holder.description.setTextColor(textColor);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindSectionView(View view, Context context, int i, Object o) {
        Pair<String, String> pair = (Pair<String, String>) o;
        CategoryViewHolder holder = (CategoryViewHolder) view.getTag();
        holder.name.setText(pair.first);
        holder.description.setText(pair.second);
    }

    @Override
    protected View newItemView(Context context, Cursor cursor, ViewGroup parent) {
        View view = getLayoutInflater().inflate(R.layout.list_item_menu_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        holder.name.setTextColor(textColor);
        holder.price.setTextColor(textColor);
        holder.description.setTextColor(textColor);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindItemView(View view, Context context, Cursor cursor) {
        ItemViewHolder holder = (ItemViewHolder) view.getTag();

        CharSequence name = cursor.getString(Query.NAME);
        CharSequence price = NumberFormat.getCurrencyInstance(Locale.US).format(cursor.getDouble(Query.PRICE));
        CharSequence description = cursor.getString(Query.DESCRIPTION);

        boolean isAvailable = cursor.getInt(Query.IS_AVAILABLE) == 1;
        if (!isAvailable) {

            StrikethroughSpan span = new StrikethroughSpan();
            name = new Truss()
                    .pushSpan(span)
                    .append(name)
                    .build();
            price = new Truss()
                    .pushSpan(span)
                    .append(price)
                    .build();
            if (description != null) {
                description = new Truss()
                        .pushSpan(span)
                        .append(description)
                        .build();
            }
        }

        holder.name.setText(name);
        holder.price.setText(price);
        holder.description.setText(description);
    }

    interface Query {

        static final String[] PROJECTION = new String[]{
                PublicContract.Menu._ID,
                PublicContract.Menu.MENU_ITEM_ID,
                PublicContract.Menu.MENU_ITEM_NAME,
                PublicContract.Menu.PRICE,
                PublicContract.Menu.IS_AVAILABLE,
                PublicContract.Menu.CATEGORY_NAME,
                PublicContract.Menu.MENU_ITEM_NOTES,
                PublicContract.Menu.CATEGORY_NOTES
        };
        static final int ID = 1;
        static final int NAME = 2;
        static final int PRICE = 3;
        static final int IS_AVAILABLE = 4;
        static final int CATEGORY_NAME = 5;
        static final int DESCRIPTION = 6;
        static final int CATEGORY_NOTES = 7;
    }

    static class ItemViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.price)
        TextView price;
        @InjectView(R.id.description)
        TextView description;

        private ItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class CategoryViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.description)
        TextView description;

        private CategoryViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
