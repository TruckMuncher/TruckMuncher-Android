package com.truckmuncher.truckmuncher.dagger;

import android.content.Context;

public final class Modules {

    private Modules() {
        // No instances
    }

    public static Object[] list(Context context) {
        return new Object[]{new NetworkModule(context)};
    }
}
