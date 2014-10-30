package com.truckmuncher.truckmuncher.data;

import android.accounts.Account;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.truckmuncher.BuildConfig;
import com.truckmuncher.truckmuncher.authentication.AccountGeneral;

import java.io.File;
import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.WireConverter;
import timber.log.Timber;

public abstract class ApiManager implements ComponentCallbacks2 {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    private static ApiManager manager;

    protected final OkHttpClient client;
    private RestAdapter adapter;
    private TruckService truckService;
    private MenuService menuService;

    protected ApiManager(Context context) {
        client = new OkHttpClient();
        configureHttpCache(context, client);
        configureSslSocketFactory(client);
        configureHostnameVerifier(client);
        adapter = configureRestAdapter(context, client).build();

        context.registerComponentCallbacks(this);
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (manager == null) {
            manager = new ApiManagerImpl(context);
        }
        return manager;
    }

    public static TruckService getTruckService(Context context) {
        return getInstance(context).getTruckService();
    }

    public static MenuService getMenuService(Context context) {
        return getInstance(context).getMenuService();
    }

    protected void configureHostnameVerifier(OkHttpClient client) {
        // No-op
    }

    protected void configureSslSocketFactory(OkHttpClient client) {
        // No-op
    }

    protected void configureHttpCache(Context context, OkHttpClient client) {
        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(context.getApplicationContext().getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }
    }

    protected RestAdapter.Builder configureRestAdapter(Context context, OkHttpClient client) {
        Account account = AccountGeneral.getStoredAccount(context);
        return new RestAdapter.Builder()
                .setRequestInterceptor(new ApiRequestInterceptor(context, account))
                .setConverter(new WireConverter())
                .setEndpoint(BuildConfig.API_ENDPOINT)
                .setClient(new OkClient(client))
                .setErrorHandler(new ApiErrorHandler(context));
    }

    protected TruckService getTruckService() {
        if (truckService == null) {
            truckService = getAdapter().create(TruckService.class);
        }
        return truckService;
    }

    protected MenuService getMenuService() {
        if (menuService == null) {
            menuService = getAdapter().create(MenuService.class);
        }
        return menuService;
    }

    protected void reset() {
        truckService = null;
        menuService = null;
    }

    protected RestAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // No-op
    }

    @Override
    public void onLowMemory() {
        onTrimMemory(TRIM_MEMORY_COMPLETE);
    }

    @Override
    public void onTrimMemory(int level) {
        if (level >= TRIM_MEMORY_MODERATE) {
            reset();
        }
    }
}
