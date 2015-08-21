package com.example.alan.rate_this_place.pasivedatacollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Xue Fei on 3/6/2015.
 */
public class StartUpBootReceiver  extends BroadcastReceiver {

    @Override

    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Intent startServiceIntent = new Intent(context, SensorListenerService.class);
            context.startService(startServiceIntent);
        }
    }

}