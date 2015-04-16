package com.truckmuncher.app;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.truckmuncher.app.common.FabricKits;
import com.truckmuncher.app.common.LoggerStarter;
import com.truckmuncher.app.dagger.Modules;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private ObjectGraph objectGraph;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Twitter should come before the graph because of logging
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET);
        Fabric.with(this, FabricKits.list(authConfig));

        LoggerStarter.start(this);
        objectGraph = ObjectGraph.create(Modules.list(this));
        FacebookSdk.sdkInitialize(this);
    }

    public void inject(Object injectable) {
        objectGraph.inject(injectable);
    }

    public ObjectGraph appGraph() {
        return objectGraph;
    }
}
