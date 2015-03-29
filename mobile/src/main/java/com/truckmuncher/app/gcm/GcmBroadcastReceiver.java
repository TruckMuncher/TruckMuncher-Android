package com.truckmuncher.app.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import timber.log.Timber;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Timber.i("Got a gcm message");
        // FIXME need to figure out what to do when we get a GCM message. This is not yet implemented on the server.
    }
}
