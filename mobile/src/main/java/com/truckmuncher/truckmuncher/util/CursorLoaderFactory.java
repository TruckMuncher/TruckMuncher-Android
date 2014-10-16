package com.truckmuncher.truckmuncher.util;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.support.annotation.NonNull;

public final class CursorLoaderFactory {

    private CursorLoaderFactory() {
        // No instances
    }

    public static CursorLoader create(@NonNull Context context, Uri uri, String[] projection) {
        return new CursorLoader(context, uri, projection, null, null, null);
    }
}