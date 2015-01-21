package com.truckmuncher.truckmuncher;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.truckmuncher.truckmuncher.dagger.Modules;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private ObjectGraph graph;

    public static void inject(Context context, Object target) {
        App app = (App) context.getApplicationContext();
        app.graph.inject(target);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        LoggerStarter.start(this);
        graph = ObjectGraph.create(Modules.list(this));
    }
}
