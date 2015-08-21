package com.example.alan.rate_this_place.pasivedatacollection;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Xue Fei on 21/8/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    protected static final String SyncAdapter_TAG = "SyncAdapter";

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(SyncAdapter_TAG, "uploading");
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onSyncCanceled(Thread thread) {
        super.onSyncCanceled(thread);
    }
}
