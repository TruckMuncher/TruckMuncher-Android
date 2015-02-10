package com.truckmuncher.app.vendor.menuadmin;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.support.annotation.NonNull;

import com.truckmuncher.app.data.Contract;
import com.truckmuncher.app.data.PublicContract;
import com.truckmuncher.app.test.VerifiableContentProvider;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.ArrayList;

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
        provider.enqueue(new VerifiableContentProvider.ApplyBatchEvent() {
            @NonNull
            @Override
            public ContentProviderResult[] onApplyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
                assertThat(operations).hasSize(1);
                assertThat(Contract.isSyncToNetwork(operations.get(0).getUri())).isTrue();
                return new ContentProviderResult[0];
            }
        });

        Intent intent = MenuItemDiffService.newIntent(Robolectric.application, new ContentValues[]{new ContentValues()});
        MenuItemDiffService service = new MenuItemDiffService();
        service.onHandleIntent(intent);

        provider.assertThatCursorsAreClosed();
        provider.assertThatQueuesAreEmpty();
    }
}
