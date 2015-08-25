package com.example.alan.rate_this_place;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alan.rate_this_place.feedback.FeedbackDialogFragment;
import com.example.alan.rate_this_place.mapview.MapsActivity;
import com.example.alan.rate_this_place.myrewards.MyRewardActivity;
import com.example.alan.rate_this_place.pasivedatacollection.SensorListenerService;
import com.example.alan.rate_this_place.ratethisplace.RateThisPlaceActivity;
import com.example.alan.rate_this_place.usersetting.UserAgreementDialogFragment;
import com.example.alan.rate_this_place.usersetting.UserProfileActivity;
import com.example.alan.rate_this_place.utility.DataLogger;
import com.example.alan.rate_this_place.visitedplace.GeofencingService;
import com.example.alan.rate_this_place.visitedplace.VisitedPlacesActivity;

import java.util.regex.Pattern;

/**
 * Created by Xue Fei on 19/5/2015.
 */

public class MainActivity extends AppCompatActivity   {

    protected static final String FirstRun_TAG = "FirstRun";
    protected static final String ActionBar_TAG = "ActionBar";
    protected static final String GoogleSignIn_TAG = "GoogleSignIn";
    protected static final String Toolbar_TAG = "Toolbar";
    protected static final String GPS_Internet_Check_TAG = "GPS_Internet_Check";


    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
              /*  case R.id.action_edit:
                    msg += "Click edit";
                    break;
                case R.id.action_share:
                    msg += "Click share";
                    break;
                case R.id.action_settings:
                    msg += "Click setting";
                    break;*/
            }

            if(!msg.equals("")) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            Log.i(Toolbar_TAG, "toolbar_click");
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        startService(new Intent(this, GeofencingService.class));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkNetworkandGPS();
        checkFirstRun();
        ReadGoogleAccount();
        DataLogger.CheckAndCreateFolder(String.valueOf("RateThisPlace"));
        DataLogger.CheckAndCreateFolder(String.valueOf("RateThisPlace"+"/"+"PassiveData"));
        DataLogger.CheckAndCreateFolder(String.valueOf("RateThisPlace"+"/"+"ActiveData"));
        DataLogger.CheckAndCreateFolder(String.valueOf("RateThisPlace"+"/"+"PendingToSend"));

    }

    @Override
    public void onStart() {
        super.onStart();
        checkNetworkandGPS();


       // Intent intent = new Intent(this, SensorListenerService.class);
       // startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
       // Intent intent = new Intent(this, SensorListenerService.class);
       // startService(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Intent intent = new Intent(this, SensorListenerService.class);
       // stopService(intent);

    }

    @Override
    protected void onStop() {
        super.onStop();

        //Intent intent = new Intent(this, SensorListenerService.class);
       // stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

      //  Intent intent = new Intent(this, SensorListenerService.class);
       // stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        switch (item.getItemId()) {
            case R.id.action_home:
                break;
            case R.id.action_mapview:
                if (isConnectingToInternet()){
                    startActivity(new Intent(this, MapsActivity.class));
                }
                else{
                    Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.action_userprofile:
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.action_aboutus:

                break;
            case R.id.action_feedback:
                new FeedbackDialogFragment().show(getSupportFragmentManager(), "FeedbackDialog");
                break;
        }




        return super.onOptionsItemSelected(item);
    }


    public void clickImage_activity_log(View view) {
      /*  Toast.makeText(this, "Activity_log", Toast.LENGTH_SHORT).show();*/
        //DataLogger.writeTolog("_________________________________start_a_new_test____________________________" + "\n");
        Intent intent = new Intent(this, VisitedPlacesActivity.class);
        startActivity(intent);

    }


    public void clickImage_myreward(View view) {


       // Intent mServiceIntent = new Intent(this, PassiveDataToFTPIntentService.class);
       // startService(mServiceIntent);
       /* Toast.makeText(this, "uploading", Toast.LENGTH_SHORT).show();
        AsyncTaskUploadFilesToFTP myfileuploader = new AsyncTaskUploadFilesToFTP(this);
        myfileuploader.execute();*/
        Intent intent = new Intent(this, MyRewardActivity.class);
        startActivity(intent);



    }


    public void clickImage_rate_this_place(View view) {
       // Toast.makeText(this, "Image_rate_this_place", Toast.LENGTH_SHORT).show();
        //DataLogger.writeTolog("_________________________________start_a_new_test____________________________"+"\n");
        Intent intent = new Intent(this, RateThisPlaceActivity.class);
        intent.putExtra("From","MainActivity");
        startActivity(intent);

    }



    public void checkFirstRun() {
        boolean DoesUserAgree = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("DoesUserAgree", false);

        if (DoesUserAgree){
            // Place your dialog code here to display the dialog

            Log.i(FirstRun_TAG, "User  agree");
            Intent intent = new Intent(this, SensorListenerService.class);
            startService(intent);
        }
        else{
            Log.i(FirstRun_TAG, "User have not agree yet");
            UserAgreementDialogFragment UserAgreement = new UserAgreementDialogFragment();;
            UserAgreement.show(getSupportFragmentManager(), "NoticeDialogFragment");
        }
    }

    public String ReadGoogleAccount() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        String possibleEmail = null;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
                Log.i("GoogleAccount", possibleEmail);
            }
        }

        this.getSharedPreferences("UserInfo", this.MODE_PRIVATE)
                .edit()
                .putString("UserID",possibleEmail)
                .apply();

        ((TextView)findViewById(R.id.textView_UserID)).setText("UserID: "+possibleEmail);
        return possibleEmail;

    }


    public void checkNetworkandGPS()
    {

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Log.i(GPS_Internet_Check_TAG, "GPS Yes");
            if (isConnectingToInternet())
            {
                ((TextView)findViewById(R.id.textView8)).setText("");
                Log.i(GPS_Internet_Check_TAG, "internet Yes");
            }
            else{
                ((TextView)findViewById(R.id.textView8)).setText("Internet is not available");

                Log.i(GPS_Internet_Check_TAG, "internet No");
            }
        }
        else{

            Log.i(GPS_Internet_Check_TAG, "GPS No");
            if (isConnectingToInternet())
            {
                ((TextView)findViewById(R.id.textView8)).setText("GPS is off");
                Log.i(GPS_Internet_Check_TAG, "internet Yes");
            }
            else{
                ((TextView)findViewById(R.id.textView8)).setText("Internet is not available and GPS is off");

                Log.i(GPS_Internet_Check_TAG, "internet No");
            }

        }


    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

}
