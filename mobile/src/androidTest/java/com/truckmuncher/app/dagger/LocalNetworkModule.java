package com.truckmuncher.app.dagger;

import android.accounts.Account;
import android.content.Context;

import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.app.data.AuthErrorHandler;
import com.truckmuncher.app.data.AuthRequestInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

@Module(overrides = true, library = true, complete = false)
public class LocalNetworkModule {

    private final MockWebServer server;
    private final Context appContext;

    public LocalNetworkModule(Context context, MockWebServer server) {
        this.server = server;
        this.appContext = context.getApplicationContext();
    }

    @Singleton
    @Provides
    public RestAdapter provideRestAdapter(RestAdapter.Builder builder) {
        return builder
                .setEndpoint(server.getUrl("/").toString())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(new MainThreadExecutor(), new MainThreadExecutor())
                .build();
    }

    @Singleton
    @Provides
    public AuthService provideAuthService(RestAdapter.Builder builder, Account account) {
        builder.setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new AuthErrorHandler(appContext))
                .setRequestInterceptor(new AuthRequestInterceptor(appContext, account))
                .setEndpoint(server.getUrl("/").toString())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(new MainThreadExecutor(), new MainThreadExecutor());
        return builder.build().create(AuthService.class);
    }
}
