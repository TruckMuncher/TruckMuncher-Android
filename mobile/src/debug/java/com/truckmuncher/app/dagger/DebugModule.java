package com.truckmuncher.app.dagger;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.squareup.okhttp.OkHttpClient;
import com.truckmuncher.app.BuildConfig;

import dagger.Module;
import dagger.Provides;

@Module(overrides = true, library = true)
public class DebugModule {

    private final Context context;

    public DebugModule(Context context) {
        this.context = context.getApplicationContext();
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    @Provides
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        NetworkModule.configureHttpCache(context, client);
        if (BuildConfig.DEBUG) {
            NetworkModule.configureSsl(client);
        }
        return client;
    }
}
