package com.truckmuncher.app.vendor.menuadmin;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.truckmuncher.app.R;
import com.truckmuncher.app.data.PublicContract;
import com.twotoasters.sectioncursoradapter.SectionCursorAdapter;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
        return getLayoutInflater().inflate(R.layout.list_item_menu_category, viewGroup, false);
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
        String price = NumberFormat.getCurrencyInstance(Locale.US).format(cursor.getDouble(Query.PRICE));
        holder.price.setText(price);

        // In stock
        holder.isAvailable.setChecked(cursor.getInt(Query.IS_AVAILABLE) == 1);
        final String internalId = cursor.getString(Query.ID);
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

    @NonNull
    Map<String, Boolean> getMenuItemAvailabilityDiff() {
        return new HashMap<>(diff);
    }

    void clearMenuItemAvailabilityDiff() {
        diff.clear();
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
        @InjectView(R.id.isAvailableSwitch)
        SwitchCompat isAvailable;
        CharArrayBuffer nameBuffer = new CharArrayBuffer(128);

        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
