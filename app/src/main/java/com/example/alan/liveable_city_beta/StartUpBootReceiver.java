package com.example.alan.liveable_city_beta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Xue Fei on 3/6/2015.
 */
public class StartUpBootReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("startuptest", "StartUpBootReceiver " + intent.getAction());

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED");
            Intent startServiceIntent = new Intent(context, SensorListenerService.class);
            context.startService(startServiceIntent);
        }
    }
}