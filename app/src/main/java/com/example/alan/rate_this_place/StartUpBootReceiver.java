package com.example.alan.rate_this_place;

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
            boolean DoesUserAgree = context.getSharedPreferences("PREFERENCE", context.MODE_PRIVATE).getBoolean("DoesUserAgree", true);
            if (DoesUserAgree == true){
                Intent startServiceIntent = new Intent(context, SensorListenerService.class);
                context.startService(startServiceIntent);
            };

        }
    }

}