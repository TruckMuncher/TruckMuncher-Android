package com.truckmuncher.app.customer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.balysv.materialripple.MaterialRippleLayout;
import com.truckmuncher.app.R;

public class TabsPagerAdapter extends FragmentPagerAdapter
        implements PagerSlidingTabStrip.CustomTabProvider {

    private final int[] TITLES = {
            R.string.tab_all_trucks,
            R.string.tab_favorite_trucks};
    private Context context;

    public TabsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(TITLES[position]);
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllTrucksGridFragment();
            case 1:
                return new FavoriteTrucksGridFragment();
        }

        return null;
    }

    @Override
    public View getCustomTabView(ViewGroup viewGroup, int i) {
        MaterialRippleLayout materialRippleLayout = (MaterialRippleLayout)
                LayoutInflater.from(context).inflate(R.layout.custom_tab, viewGroup, false);
        materialRippleLayout.setLayoutParams(
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f / getCount()));
        TextView textView = (TextView) materialRippleLayout.findViewById(R.id.tab_title);
        textView.setText(context.getString(TITLES[i]));
        return materialRippleLayout;
    }
}

