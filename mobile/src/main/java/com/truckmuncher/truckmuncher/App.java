package com.truckmuncher.truckmuncher;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerStarter.start(this);
    }
}
