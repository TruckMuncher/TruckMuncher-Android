package com.truckmuncher.truckmuncher.dagger;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

@Module(overrides = true, library = true, complete = false)
public class LocalNetworkModule {

    private final MockWebServer server;

    public LocalNetworkModule(MockWebServer server) {
        this.server = server;
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
}
