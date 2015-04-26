package com.truckmuncher.app.customer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truckmuncher.app.R;
import com.truckmuncher.app.common.Truss;
import com.truckmuncher.app.data.PublicContract;
import com.twotoasters.sectioncursoradapter.SectionCursorAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * @see <a href="https://github.com/twotoasters/SectionCursorAdapter">GitHub Project</a>
 */
public class MenuAdapter extends SectionCursorAdapter {

    private final int textColor;
    private final Typeface fontFamily;

    public MenuAdapter(Context context, int textColor) {
        super(context, null, 0);
        this.textColor = textColor;
        fontFamily = Typeface.createFromAsset(context.getAssets(), "flaticon.ttf");
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
        holder.tagsView.setTextColor(textColor);
        holder.tagsView.setTypeface(fontFamily);
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

        String tagsText = cursor.getString(Query.TAGS);
        Timber.d("Text: (%s)", tagsText);
        if (!TextUtils.isEmpty(tagsText)) {
            List<String> tags = PublicContract.convertStringToList(tagsText);
            holder.tags = MenuItemTag.convertToTags(tags);
        }

        if (holder.tags != null && !holder.tags.isEmpty()) {
            StringBuilder tagsBuilder = new StringBuilder();
            for (MenuItemTag tag : holder.tags) {
                tagsBuilder.append(tag.fontCharacter);
            }
            holder.tagsView.setText(tagsBuilder.toString());
            holder.tagsView.setVisibility(View.VISIBLE);
        } else {
            holder.tagsView.setVisibility(View.GONE);
            holder.tagsView.setText(null);
            holder.tags = null;
        }
    }

    static enum MenuItemTag {
        /*
         * These names have to match the keys used by the web, otherwise we won't match the tags correctly
         */
        GLUTEN("gluten free", "\ue004", R.string.menu_item_tag_gluten),
        VEGETARIAN("vegetarian", "\ue000", R.string.menu_item_tag_vegetarian),
        VEGAN("vegan", "\ue001", R.string.menu_item_tag_vegan),
        PEANUTS("contains peanuts", "\ue002", R.string.menu_item_tag_peanuts),
        RAW("raw", "\ue003", R.string.menu_item_tag_raw);

        final String apiKey;
        final String fontCharacter;
        @StringRes
        final int description;

        private MenuItemTag(String apiKey, String fontCharacter, @StringRes int description) {
            this.apiKey = apiKey;
            this.fontCharacter = fontCharacter;
            this.description = description;
        }

        static List<MenuItemTag> convertToTags(List<String> tags) {
            List<MenuItemTag> list = new ArrayList<>(tags.size());
            for (MenuItemTag menuItemTag : MenuItemTag.values()) {
                if (tags.contains(menuItemTag.apiKey)) {
                    list.add(menuItemTag);
                }
            }
            return list;
        }
    }

    interface Query {

        String[] PROJECTION = new String[]{
                PublicContract.Menu._ID,
                PublicContract.Menu.MENU_ITEM_ID,
                PublicContract.Menu.MENU_ITEM_NAME,
                PublicContract.Menu.PRICE,
                PublicContract.Menu.IS_AVAILABLE,
                PublicContract.Menu.CATEGORY_NAME,
                PublicContract.Menu.MENU_ITEM_NOTES,
                PublicContract.Menu.CATEGORY_NOTES,
                PublicContract.Menu.MENU_ITEM_TAGS
        };
        int ID = 1;
        int NAME = 2;
        int PRICE = 3;
        int IS_AVAILABLE = 4;
        int CATEGORY_NAME = 5;
        int DESCRIPTION = 6;
        int CATEGORY_NOTES = 7;
        int TAGS = 8;
    }

    static class ItemViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.price)
        TextView price;
        @InjectView(R.id.tags)
        TextView tagsView;
        @InjectView(R.id.description)
        TextView description;
        List<MenuItemTag> tags;

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
