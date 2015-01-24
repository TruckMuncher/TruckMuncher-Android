package com.truckmuncher.app.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

public class SyncService extends Service {
    // Object to use as a thread-safe lock
    private static final Object syncAdapterLock = new Object();
    // Storage for an instance of the sync adapter
    private static SyncAdapter syncAdapter = null;

    /*
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true, false);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     */
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        return syncAdapter.getSyncAdapterBinder();
    }
}