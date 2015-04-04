package com.truckmuncher.app.dagger;

import android.app.Application;

public final class Modules {
    private Modules() {
        // No instances
    }

    public static Object[] list(Application app) {
        return new Object[] {
                new UserModule(app),
                new NetworkModule(app)
        };
    }
}
