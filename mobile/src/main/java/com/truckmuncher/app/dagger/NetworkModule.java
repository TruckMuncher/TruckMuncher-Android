package com.truckmuncher.app.dagger;

import android.accounts.Account;
import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.api.menu.MenuService;
import com.truckmuncher.api.search.SearchService;
import com.truckmuncher.api.trucks.TruckService;
import com.truckmuncher.app.BuildConfig;
import com.truckmuncher.app.customer.ActiveTrucksService;
import com.truckmuncher.app.customer.GetTruckProfilesService;
import com.truckmuncher.app.customer.SimpleSearchService;
import com.truckmuncher.app.data.ApiErrorHandler;
import com.truckmuncher.app.data.AuthErrorHandler;
import com.truckmuncher.app.data.AuthRequestInterceptor;
import com.truckmuncher.app.data.AuthenticatedRequestInterceptor;
import com.truckmuncher.app.data.PRNGFixes;
import com.truckmuncher.app.data.sync.SyncAdapter;
import com.truckmuncher.app.menu.MenuUpdateService;
import com.truckmuncher.app.vendor.VendorTrucksService;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.WireConverter;
import timber.log.Timber;

@Module(injects = {
        SimpleSearchService.class,
        GetTruckProfilesService.class,
        MenuUpdateService.class,
        SyncAdapter.class,
        VendorTrucksService.class,
        ActiveTrucksService.class
},
        complete = false)
public class NetworkModule {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    private final Context appContext;

    public NetworkModule(Context context) {
        appContext = context.getApplicationContext();
        if (!BuildConfig.DEBUG) {   // Work around for robolectric
            PRNGFixes.apply();  // Fix SecureRandom
        }
    }

    protected static void configureHttpCache(Context context, OkHttpClient client) {
        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(context.getApplicationContext().getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }
    }

    @Provides
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        configureHttpCache(appContext, client);
        return client;
    }

    @Provides
    public RestAdapter.Builder provideRestAdapterBuilder(OkHttpClient client) {
        return new RestAdapter.Builder()
                .setRequestInterceptor(new AuthenticatedRequestInterceptor(appContext))
                .setConverter(new WireConverter())
                .setEndpoint(BuildConfig.API_ENDPOINT)
                .setClient(new OkClient(client))
                .setErrorHandler(new ApiErrorHandler(appContext));
    }

    @Singleton
    @Provides
    public RestAdapter provideRestAdapter(RestAdapter.Builder builder) {
        return builder.build();
    }

    @Singleton
    @Provides
    public TruckService provideTruckService(RestAdapter adapter) {
        return adapter.create(TruckService.class);
    }

    @Singleton
    @Provides
    public MenuService provideMenuService(RestAdapter adapter) {
        return adapter.create(MenuService.class);
    }

    @Singleton
    @Provides
    public AuthService provideAuthService(RestAdapter.Builder builder, Account account) {
        builder.setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new AuthErrorHandler(appContext))
                .setRequestInterceptor(new AuthRequestInterceptor(appContext, account));
        return builder.build().create(AuthService.class);
    }

    @Singleton
    @Provides
    public SearchService provideSearchService(RestAdapter adapter) {
        return adapter.create(SearchService.class);
    }
}
