package com.chesapeaketechnology.syncmonkey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chesapeaketechnology.syncmonkey.fileupload.FileUploadSyncAdapter;

/**
 * Kicks off a sync when the {@link SyncMonkeyConstants#ACTION_SYNC_NOW} broadcast is received.
 *
 * @since 0.1.3
 */
public class SyncNowBroadcastReceiver extends BroadcastReceiver
{
    private static final String LOG_TAG = SyncNowBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (null == intent) return;

        if (!SyncMonkeyConstants.ACTION_SYNC_NOW.equals(intent.getAction())) return;

        Log.i(LOG_TAG, "Kicking off a sync because a broadcast was received for the SYNC_NOW action");

        FileUploadSyncAdapter.runSyncAdapterNow(context);
    }
}
