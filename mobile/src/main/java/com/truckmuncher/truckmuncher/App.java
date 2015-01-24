package com.truckmuncher.truckmuncher;

import android.app.Application;
import android.content.Context;

import com.truckmuncher.truckmuncher.dagger.Modules;

import dagger.ObjectGraph;

public class App extends Application {

    private ObjectGraph graph;

    public static void inject(Context context, Object target) {
        App app = (App) context.getApplicationContext();
        app.graph.inject(target);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LoggerStarter.start(this);
        graph = ObjectGraph.create(Modules.list(this));
    }
}
