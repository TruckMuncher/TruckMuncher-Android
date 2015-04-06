package com.truckmuncher.app;

import android.app.Application;
import android.content.Context;

import com.truckmuncher.app.dagger.Modules;

import dagger.ObjectGraph;

public class App extends Application {

    private ObjectGraph objectGraph;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LoggerStarter.start(this);
        objectGraph = ObjectGraph.create(Modules.list(this));
    }

    public void inject(Object injectable) {
        objectGraph.inject(injectable);
    }

    public ObjectGraph appGraph() {
        return objectGraph;
    }
}
