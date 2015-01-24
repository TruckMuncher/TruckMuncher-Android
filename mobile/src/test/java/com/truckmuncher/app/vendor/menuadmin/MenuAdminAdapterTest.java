package com.truckmuncher.app.vendor.menuadmin;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;
import com.truckmuncher.app.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import butterknife.ButterKnife;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(ReadableRobolectricTestRunner.class)
public class MenuAdminAdapterTest {

    @Test
    public void sectionIsDeterminedByCategoryColumn() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(MenuAdminAdapter.Query.CATEGORY_NAME)).thenReturn("Sandwiches");
        MenuAdminAdapter adapter = new MenuAdminAdapter(Robolectric.application, cursor);
        assertThat(adapter.getSectionFromCursor(cursor)).isEqualTo("Sandwiches");
    }

    @Test
    public void adapterCanBindSectionViews() {
        Cursor cursor = mock(Cursor.class);
        MenuAdminAdapter adapter = new MenuAdminAdapter(Robolectric.application, cursor);
        View view = adapter.newSectionView(Robolectric.application, null, null);
        TextView text = ButterKnife.findById(view, android.R.id.text1);
        assertThat(text.getText()).isEmpty();

        adapter.bindSectionView(view, Robolectric.application, 0, "Sandwiches");
        assertThat(text.getText()).isEqualTo("Sandwiches");
    }

    @Test
    public void itemViewsUseViewHolder() {
        Cursor cursor = mock(Cursor.class);
        MenuAdminAdapter adapter = new MenuAdminAdapter(Robolectric.application, cursor);
        View view = adapter.newItemView(Robolectric.application, cursor, null);
        assertThat(view.getTag()).isNotNull();
    }

    @Test
    public void adapterCanBindItemViews() {
        MatrixCursor cursor = new MatrixCursor(MenuAdminAdapter.Query.PROJECTION);
        cursor.addRow(new Object[]{1, "ID", "BLT", 6.55, 1, "Sandwiches"});
        MenuAdminAdapter adapter = new MenuAdminAdapter(Robolectric.application, cursor);
        cursor.moveToFirst();
        View view = adapter.newItemView(Robolectric.application, cursor, null);
        adapter.bindItemView(view, Robolectric.application, cursor);

        assertThat(ButterKnife.<TextView>findById(view, R.id.name).getText().toString()).isEqualTo("BLT");
        assertThat(ButterKnife.<TextView>findById(view, R.id.price).getText().toString()).isEqualTo("$6.55");
        assertThat(ButterKnife.<SwitchCompat>findById(view, R.id.isAvailableSwitch).isChecked()).isTrue();
    }

    @Test
    public void getMenuItemAvailabilityDiffDoesDefensiveCopy() {
        Cursor cursor = mock(Cursor.class);
        MenuAdminAdapter adapter = new MenuAdminAdapter(Robolectric.application, cursor);
        assertThat(adapter.getMenuItemAvailabilityDiff()).isNotSameAs(adapter.getMenuItemAvailabilityDiff());
    }

    @Test
    public void getMenuItemAvailabilityDiffReturnEmptyMapNotNull() {
        Cursor cursor = mock(Cursor.class);
        MenuAdminAdapter adapter = new MenuAdminAdapter(Robolectric.application, cursor);
        assertThat(adapter.getMenuItemAvailabilityDiff()).isNotNull();
    }
}
