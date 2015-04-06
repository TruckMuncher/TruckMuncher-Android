package com.truckmuncher.app.dagger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

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
import com.truckmuncher.app.data.TruckMuncherContentProvider;
import com.truckmuncher.app.data.sql.SqlOpenHelper;
import com.truckmuncher.app.data.sync.SyncAdapter;
import com.truckmuncher.app.menu.MenuUpdateService;
import com.truckmuncher.app.vendor.VendorHomeActivity;
import com.truckmuncher.app.vendor.VendorTrucksService;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.WireConverter;

// TODO rename. Does more than just networking now
@Module(injects = {
        ActiveTrucksService.class,
        GetTruckProfilesService.class,
        MenuUpdateService.class,
        SimpleSearchService.class,
        SyncAdapter.class,
        TruckMuncherContentProvider.class,
        VendorHomeActivity.class,
        VendorTrucksService.class
}, includes = UserModule.class)
public class NetworkModule {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    private final Context appContext;

    public NetworkModule(Context context) {
        appContext = context.getApplicationContext();
        if (!BuildConfig.DEBUG) {   // Work around for robolectric
            PRNGFixes.apply();  // Fix SecureRandom
        }
    }

    public static void configureHttpCache(Context context, OkHttpClient client) {
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(context.getApplicationContext().getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);
    }

    @Provides
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        configureHttpCache(appContext, client);
        return client;
    }

    @Singleton
    @Provides
    public RestAdapter.Builder provideRestAdapterBuilder(OkHttpClient client) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setConverter(new WireConverter())
                .setEndpoint(BuildConfig.API_ENDPOINT)
                .setClient(new OkClient(client));
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        return builder;
    }

    @Singleton
    @Provides
    public RestAdapter provideRestAdapter(RestAdapter.Builder builder, AccountManager accountManager) {
        return builder
                .setRequestInterceptor(new AuthenticatedRequestInterceptor(accountManager))
                .setErrorHandler(new ApiErrorHandler(appContext))
                .build();
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
    public AuthService provideAuthService(RestAdapter.Builder builder, AccountManager accountManager, @Nullable Account account) {
        builder.setErrorHandler(new AuthErrorHandler(appContext))
                .setRequestInterceptor(new AuthRequestInterceptor(accountManager, account));
        return builder.build().create(AuthService.class);
    }

    @Singleton
    @Provides
    public SearchService provideSearchService(RestAdapter adapter) {
        return adapter.create(SearchService.class);
    }

    @Singleton
    @Provides
    public SQLiteOpenHelper provideSQLiteOpenHelper() {
        return SqlOpenHelper.newInstance(appContext);
    }

    @Provides
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(appContext);
    }
}
