package com.example.alan.rate_this_place.pasivedatacollection;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.alan.rate_this_place.R;
import com.example.alan.rate_this_place.utility.Constants;
import com.example.alan.rate_this_place.utility.DataLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Xue Fei on 19/5/2015.
 */
public class SensorListenerService extends Service implements SensorEventListener, LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    protected static final String GoogleApiTAG = "GoogleApi";
    /*location*/
    LocationManager mlocationManager;
    /*sensor*/
    private SensorManager sensorManager = null;
    PowerManager.WakeLock wakeLock;


    /*battery*/
    IntentFilter ifilter;
    Intent batteryStatus;

    private boolean boolcheck=true;


    /**
     * Used when requesting or removing activity detection updates.
     */
    private PendingIntent mActivityDetectionPendingIntent;
    /**
     * Provides the entry point to Google Play services.
     */
    //protected GoogleApiClient mGoogleApiClient;
    protected static final String GoogleApi_TAG = "GoogleApi";
    protected static final String Location_TAG = "Location";
    protected static final String Sensor_TAG = "Sensor";
    protected static final String Audio_TAG = "AudioLevel";
    protected static final String WakelockTag = "Wakelock";

    private final Handler Soundlevel_handler = new Handler();


    private Runnable Soundlevel_runable = new Runnable() {
        public void run() {
            double i= soundlevel.Soundlevel_getAmplitude();
            if (i>100){
                Log.i(Audio_TAG, " mic "+String.valueOf(i));
            }

            DataLogger.writeTolog("S " + String.valueOf(i) + "\n", logswich);
            Soundlevel_handler.postDelayed(this, 1000);
        }
    };


    private final Handler Batteryhandler = new Handler();
    final Runnable Battery_runable = new Runnable() {
        public void run() {
            ReadBatteryLevel();
            Batteryhandler.postDelayed(this, 1000*60*15);
        }
    };

    //logswich
    static String logswich = "";


    /*google activity detection*/
    protected GoogleApiClient mGoogleApiClient;

    public double ACCsamplingrate=50;  //100Hz will slow the system down
    public double GROsamplingrate=50;
    public double Lightsamplingrate=0.5;


    private SoundLevelMonitor soundlevel= new SoundLevelMonitor();




    @Override
    public void onCreate() {

        // get an instance of the receiver in your service
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_ACTION);
        ActivityDetectionBroadcastReceiver mReceiver = new ActivityDetectionBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        //listen to wifi connection availability
        IntentFilter wififilter = new IntentFilter();
        wififilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //wififilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        WifiBroadcastReceiver mwifiReceiver = new WifiBroadcastReceiver(this);
        registerReceiver(mwifiReceiver, wififilter);

        boolean DoesUserAgree = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("DoesUserAgree", true);
        if (DoesUserAgree==false){stopSelf();};

        buildGoogleApiClient();
        // The service is being created

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                WakelockTag);

        wakeLock.acquire();
        Toast.makeText(this, "sensor service starting", Toast.LENGTH_SHORT).show();

        /*googleApi*/
        mGoogleApiClient.connect();

        /*sensor - read all sensors*/

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        /*List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors)
        {
            if sensor.
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }*/

        sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), (int)(1/(float)ACCsamplingrate)*1000*1000);
        sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), (int)(1/(float)GROsamplingrate)*1000*1000);
        sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), (int)(1/(float)Lightsamplingrate)*1000*1000);
        sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 1000*1000);
        sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), 1000*1000);



        /*location */
        // Acquire a reference to the system Location Manager
        mlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        mlocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this); //long minTime, float minDistance
        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        /*sound_level*/
         soundlevel.Soundlevel_start();
        Soundlevel_handler.postDelayed(Soundlevel_runable, 1000);


        /*battery_level*/
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);
        Batteryhandler.postDelayed(Battery_runable, 1000);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    /*    final Handler handler001 = new Handler();
        final Runnable r001 = new Runnable() {

            public void run() {
                logswich=Integer.toString(ACCsamplingrate);
               // Log.i("changeACC", "changeACC " + ACCsamplingrate + "logswich "+logswich);
                changeACCsamplingrate(ACCsamplingrate);
                ACCsamplingrate = ACCsamplingrate+20;
                handler001.postDelayed(this, 1000*60*30);
            }
        };

        handler001.postDelayed(r001, 1000*60);
*/


        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {

        Log.d("startuptest", "stop service ");

        Soundlevel_handler.removeCallbacks(Soundlevel_runable);
        mlocationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
        soundlevel.Soundlevel_stop();
        removeActivityUpdates();
        mGoogleApiClient.disconnect();
        wakeLock.release();
        Toast.makeText(this, "sensor service Stop", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        // The service is no longer used and is being destroyed
    }




    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor mySensor = event.sensor;


        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
           // DataLogger.writeTolog( " A " + String.format("%.3f", x) + " " + String.format("%.2f", y) + " " + String.format("%.2f", z) + " "+Long.toString(event.timestamp)+"\n");
            String dataformat= "A " + String.format("%.3f", x) + " " + String.format("%.3f", y) + " " + String.format("%.3f", z) + " "+ "\n";
            DataLogger.writeTolog( dataformat,logswich);
            Log.i(Sensor_TAG, Long.toString(event.timestamp) + dataformat);
        }
        else if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // DataLogger.writeTolog( " A " + String.format("%.2f", x) + " " + String.format("%.2f", y) + " " + String.format("%.2f", z) + " "+Long.toString(event.timestamp)+"\n");
            String dataformat= "G " + String.format("%.3f", x) + " " + String.format("%.3f", y) + " " + String.format("%.3f", z) + " "+ "\n";
            DataLogger.writeTolog( dataformat,logswich);
            Log.i(Sensor_TAG, Long.toString(event.timestamp) + dataformat);
        }
        else if (mySensor.getType() == Sensor.TYPE_LIGHT) {
            float x = event.values[0];

            // DataLogger.writeTolog( " A " + String.format("%.2f", x) + " " + String.format("%.2f", y) + " " + String.format("%.2f", z) + " "+Long.toString(event.timestamp)+"\n");
            String dataformat= "L " + String.format("%3f", x)+ "\n";
            DataLogger.writeTolog( dataformat,logswich);
            Log.i(Sensor_TAG, Long.toString(event.timestamp) + dataformat);
        }
        else if (mySensor.getType() == Sensor.TYPE_PROXIMITY) {
            float x = event.values[0];
           // DataLogger.writeTolog(" P " + x + " "+Long.toString(event.timestamp)+"\n");
            String dataformat= "P " + String.format("%f", x) + "\n";
            DataLogger.writeTolog("P " + x + " "+"\n",logswich);
            Log.i(Sensor_TAG, Long.toString(event.timestamp)+" "+ "Proximity x=" + x);
        }
        else if (mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
           // DataLogger.writeTolog( " M " + x + " " + y + " " + z + " "+Long.toString(event.timestamp)+ "\n");
            String dataformat= "M " + String.format("%.3f", x) + " " + String.format("%.3f", y) + " " + String.format("%.3f", z) + " "+ "\n";
            DataLogger.writeTolog(dataformat,logswich);
            Log.i(Sensor_TAG, Long.toString(event.timestamp) + dataformat);
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

     /*location*/

    @Override
    public void onLocationChanged(Location location) {


        double longitude = location.getLongitude();
        double latitude =  location.getLatitude();
        double Accuracy = location.getAccuracy();
        String Location_information= "L " + longitude + " " + latitude+" "+location.getProvider()+" "+Accuracy;

        Log.i(Location_TAG,  Location_information);
        DataLogger.writeTolog(Location_information + "\n",logswich);

        //Toast.makeText(this, Location_information, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*battery*/
    private float ReadBatteryLevel() {


        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        Log.i("MyActivity",  " "+"battery level " + batteryPct);
        DataLogger.writeTolog( " " + "B " + batteryPct + "\n",logswich);
        return batteryPct;
    }

    /**GoogleApiClient**/
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(Sensor_TAG, "Connected to GoogleApiClient");
        requestActivityUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(GoogleApi_TAG, "Connection suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(GoogleApi_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {

        if (status.isSuccess()) {

            Log.e(GoogleApi_TAG, "status is successful: " + status.getStatusMessage());
        } else {
            Log.e(GoogleApi_TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }

    }

    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} starts receiving callbacks when
     * activities are detected.
     */
    public void requestActivityUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            Log.i(GoogleApi_TAG, "Unable to Connected to GoogleApiClient");
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
        Log.i(GoogleApi_TAG, "request Activity Recognition Service");
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     */
    public void removeActivityUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mActivityDetectionPendingIntent != null) {

            return mActivityDetectionPendingIntent;
        }

        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     */
    public void changeACCsamplingrate(int samplingrate) {

    // if (boolcheck==true){
         boolcheck=false;
         sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    // }else{
        boolcheck=true;
        int delay= (int)((1/(float)ACCsamplingrate)*1000*1000);
         sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), delay);
        Log.i("changeACC", "changeACC " + ACCsamplingrate + "" + logswich + "delayxue" + delay);
   // }
    }


    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";
        Context from;

        ActivityDetectionBroadcastReceiver(Context context){
            from=context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);

            // Log each activity.
            for (DetectedActivity da: updatedActivities) {
                String Activity_value = Constants.getActivityString(
                        getApplicationContext(),
                        da.getType()) + " " + da.getConfidence() + "%";
                DataLogger.writeTolog("GA " + Activity_value + "\n", SensorListenerService.logswich);
                Log.i(GoogleApiTAG, Activity_value);
                if (Activity_value.equals("Still 100%")){
                   // sensorManager.unregisterListener((SensorEventListener) from,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                   // sensorManager.unregisterListener((SensorEventListener) from, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
                }
                else{
                    sensorManager.registerListener((SensorEventListener) from,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), (int)(1/(float)ACCsamplingrate)*1000*1000);
                    sensorManager.registerListener((SensorEventListener) from,  sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), (int)(1/(float)GROsamplingrate)*1000*1000);
                }
            }
        }
    }


    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";
        Context from;

        WifiBroadcastReceiver(Context context){
            from=context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
           Log.i("wifi","wifistart");

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                if (isWiFi){
                    Log.i("wifi", "wifi-upload-start");
                    File filepath= new File(Environment.getExternalStorageDirectory()+"/"+ "RateThisPlace"+"/PassiveData");
                    if (folderSize(filepath)>10000){
                        Intent mServiceIntent = new Intent(getBaseContext(), PassiveDataToFTPIntentService.class);
                        startService(mServiceIntent);
                    }

                }
            }
        }
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }




}
