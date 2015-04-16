package com.truckmuncher.app.dagger;

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
public class LocalGlobalModule {

    private final MockWebServer server;

    public LocalGlobalModule(MockWebServer server) {
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

    @Singleton
    @Provides
    public AuthService provideAuthService(RestAdapter.Builder builder, AuthErrorHandler errorHandler, AuthRequestInterceptor interceptor) {
        builder.setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(errorHandler)
                .setRequestInterceptor(interceptor)
                .setEndpoint(server.getUrl("/").toString())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(new MainThreadExecutor(), new MainThreadExecutor());
        return builder.build().create(AuthService.class);
    }
}
