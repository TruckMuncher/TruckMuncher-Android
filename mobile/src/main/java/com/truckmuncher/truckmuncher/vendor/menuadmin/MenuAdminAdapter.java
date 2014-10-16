package com.truckmuncher.truckmuncher.vendor.menuadmin;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.phrase.Phrase;
import com.truckmuncher.truckmuncher.R;
import com.twotoasters.sectioncursoradapter.SectionCursorAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.truckmuncher.truckmuncher.data.Contract.CategoryEntry;
import static com.truckmuncher.truckmuncher.data.Contract.MenuEntry;
import static com.truckmuncher.truckmuncher.data.Contract.MenuItemEntry;

/**
 * @see <a href="https://github.com/twotoasters/SectionCursorAdapter">GitHub Project</a>
 */
public class MenuAdminAdapter extends SectionCursorAdapter {

    private final Map<String, Boolean> diff = new HashMap<>();

    public MenuAdminAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    protected Object getSectionFromCursor(Cursor cursor) {
        return cursor.getString(Query.CATEGORY_NAME);
    }

    @Override
    protected View newSectionView(Context context, Object o, ViewGroup viewGroup) {
        return getLayoutInflater().inflate(android.R.layout.simple_list_item_1, viewGroup, false);
    }

    @Override
    protected void bindSectionView(View view, Context context, int i, Object o) {
        ButterKnife.<TextView>findById(view, android.R.id.text1).setText((String) o);
    }

    @Override
    protected View newItemView(Context context, Cursor cursor, ViewGroup parent) {
        View view = getLayoutInflater().inflate(R.layout.list_item_menu_item_admin, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    protected void bindItemView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // Name
        cursor.copyStringToBuffer(Query.NAME, holder.nameBuffer);
        holder.name.setText(holder.nameBuffer.data, 0, holder.nameBuffer.sizeCopied);

        // Price
        CharSequence price = Phrase.from(context, R.string.currency)
                .put("price", Float.toString(cursor.getFloat(Query.PRICE)))
                .format();
        holder.price.setText(price);

        // In stock
        holder.isAvailable.setChecked(cursor.getInt(Query.IS_AVAILABLE) == 1);
        final String internalId = cursor.getString(Query.INTERNAL_ID);
        holder.isAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // If the item has not been changed, add the new state
                Boolean state = diff.get(internalId);
                if (state == null) {
                    diff.put(internalId, isChecked);
                } else {

                    // If the item has been changed, it's now been changed back so remove from the diff
                    diff.remove(internalId);
                }
            }
        });
    }

    Map<String, Boolean> getMenuItemAvailabilityDiff() {
        return new HashMap<>(diff);
    }

    void clearMenuItemAvailabilityDiff() {
        diff.clear();
    }

    public interface Query {

        public static final String[] PROJECTION = new String[]{
                MenuEntry._ID,
                MenuItemEntry.COLUMN_INTERNAL_ID,
                MenuItemEntry.COLUMN_NAME,
                MenuItemEntry.COLUMN_PRICE,
                MenuItemEntry.COLUMN_IS_AVAILABLE,
                CategoryEntry.COLUMN_NAME
        };
        static final int INTERNAL_ID = 1;
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
        @InjectView(R.id.isAvailableSwitch)
        Switch isAvailable;
        CharArrayBuffer nameBuffer = new CharArrayBuffer(128);

        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
