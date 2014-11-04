package com.truckmuncher.truckmuncher.data;

import android.content.Context;

import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.truckmuncher.App;

import javax.inject.Inject;

// FIXME Delete this class before merging
public final class ApiManager {

    private ApiManager() {
        // No instances
    }

    /**
     * @deprecated Instead you should use an {@code @Inject} annotation
     */
    @Deprecated
    public static TruckService getTruckService(Context context) {
        Shim shim = new Shim();
        App.inject(context, shim);
        return shim.truckService;
    }

    /**
     * @deprecated Instead you should use an {@code @Inject} annotation
     */
    @Deprecated
    public static MenuService getMenuService(Context context) {
        Shim shim = new Shim();
        App.inject(context, shim);
        return shim.menuService;
    }

    public static final class Shim {
        @Inject
        TruckService truckService;
        @Inject
        MenuService menuService;
    }
}
