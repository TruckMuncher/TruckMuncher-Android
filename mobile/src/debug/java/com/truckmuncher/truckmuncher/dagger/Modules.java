package com.truckmuncher.truckmuncher.dagger;

import android.content.Context;

import com.truckmuncher.truckmuncher.BuildConfig;

public final class Modules {

    private Modules() {
        // No instances
    }

    public static Object[] list(Context context) {
        if (BuildConfig.FLAVOR.equals("mock")) {
            return new Object[]{new NetworkModule(context)
                    , new MockNetworkModule()
                    , new UserModule(context)};
        } else {
            return new Object[]{new NetworkModule(context)
                    , new DebugNetworkModule(context)
                    , new UserModule(context)};
        }
    }
}
