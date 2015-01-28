package com.truckmuncher.app.test;

import android.content.res.Resources;
import android.support.annotation.IdRes;

public final class Ids {

    private Ids() {
        // No instances
    }

    @IdRes
    public static int title() {
        return Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    }
}
