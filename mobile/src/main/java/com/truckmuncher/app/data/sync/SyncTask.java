package com.truckmuncher.app.data.sync;

import android.content.SyncResult;
import android.os.RemoteException;

public abstract class SyncTask {

    public final void execute(SyncResult syncResult) {
        try {
            ApiResult apiResult = sync(syncResult);
            translateApiResultToSyncResult(apiResult, syncResult);
        } catch (RemoteException e) {
            syncResult.databaseError = true;
        }
    }

    protected abstract ApiResult sync(SyncResult syncResult) throws RemoteException;

    private void translateApiResultToSyncResult(ApiResult apiResult, SyncResult syncResult) {
        switch (apiResult) {
            case NEEDS_USER_INPUT:
                syncResult.tooManyRetries = true;
                break;
            case SHOULD_RETRY:
                syncResult.fullSyncRequested = true;
                break;
            case TEMPORARY_ERROR:
                syncResult.stats.numIoExceptions++;
                break;
            case PERMANENT_ERROR:
                syncResult.stats.numParseExceptions++;
                break;
            case OK:
                // No-op
                break;
            default:
                throw new IllegalStateException("Got an unhandled ApiResult type");
        }
    }
}
