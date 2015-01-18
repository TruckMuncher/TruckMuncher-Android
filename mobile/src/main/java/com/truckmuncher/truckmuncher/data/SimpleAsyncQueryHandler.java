package com.truckmuncher.truckmuncher.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

/**
 * Simple class to not require you to subclass {@link android.content.AsyncQueryHandler} in code.
 */
public class SimpleAsyncQueryHandler extends AsyncQueryHandler {

    public SimpleAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }
}
