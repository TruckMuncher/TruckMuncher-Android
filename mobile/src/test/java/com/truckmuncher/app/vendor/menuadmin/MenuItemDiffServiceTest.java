package com.truckmuncher.app.vendor.menuadmin;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;
import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.test.VerifiableContentProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class MenuItemDiffServiceTest {

    @Test
    public void newIntentBuildsAsExpected() {
        ContentValues blt = new ContentValues();
        blt.put(PublicContract.Menu.MENU_ITEM_NAME, "BLT");
        ContentValues[] valuesList = new ContentValues[]{blt};
        Intent intent = MenuItemDiffService.newIntent(Robolectric.application, valuesList);

        assertThat(intent.getComponent().getClassName()).isEqualTo(MenuItemDiffService.class.getName());
        assertThat(intent.getExtras().get("content_values")).isNotNull();
    }

    @Test
    public void onHandleIntentPerformsBulkInsertWithSyncToNetworkDirective() {
        VerifiableContentProvider provider = new VerifiableContentProvider();
        ShadowContentResolver.registerProvider(PublicContract.CONTENT_AUTHORITY, provider);
        provider.enqueue(new VerifiableContentProvider.BulkInsertEvent() {
            @Override
            public int onBulkInsert(Uri uri, @NonNull ContentValues[] values) {
                assertThat(Contract.isSyncToNetwork(uri)).isTrue();
                return 0;
            }
        });

        Intent intent = MenuItemDiffService.newIntent(Robolectric.application, new ContentValues[0]);
        MenuItemDiffService service = new MenuItemDiffService();
        service.onHandleIntent(intent);

        provider.assertThatCursorsAreClosed();
        provider.assertThatQueuesAreEmpty();
    }
}