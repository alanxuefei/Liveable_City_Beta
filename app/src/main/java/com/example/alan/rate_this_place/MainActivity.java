package com.example.alan.rate_this_place;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.regex.Pattern;

/**
 * Created by Xue Fei on 19/5/2015.
 */

public class MainActivity extends AppCompatActivity   {

    protected static final String FirstRun_TAG = "FirstRun";
    protected static final String ActionBar_TAG = "ActionBar";
    protected static final String GoogleSignIn_TAG = "GoogleSignIn";
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 0;

    private void showDialog(String s) {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);





       // checkFirstRun();
        ReadGoogleAccount();
        DataLogger.CheckAndCreateFolder(String.valueOf(R.string.app_name));
        DataLogger.CheckAndCreateFolder(String.valueOf(R.string.app_name+"/"+"PassiveData"));
        DataLogger.CheckAndCreateFolder(String.valueOf(R.string.app_name+"/"+"ActiveData"));

    }

    @Override
    public void onStart() {
        super.onStart();

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

        int id = item.getItemId();
        Log.i(ActionBar_TAG, "clicked");

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

        //this.finish();
        //System.exit(0);
      //  Intent intent = new Intent(this, SensorListenerService.class);
       // stopService(intent);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void clickImage_activity_log(View view) {
      /*  Toast.makeText(this, "Activity_log", Toast.LENGTH_SHORT).show();*/
        //DataLogger.writeTolog("_________________________________start_a_new_test____________________________" + "\n");
        Intent intent = new Intent(this, HumanActivityDiaryActivity.class);
        startActivity(intent);




    }


    public void clickImage_fpt(View view) {


      /*  Intent mServiceIntent = new Intent(this, IntentServiceFTP.class);
        startService(mServiceIntent);*/
        Toast.makeText(this, "uploading", Toast.LENGTH_SHORT).show();
        AsyncTaskUploadFilesToFTP myfileuploader = new AsyncTaskUploadFilesToFTP(this);
        myfileuploader.execute();


    }


    public void clickImage_rate_this_place(View view) {
       // Toast.makeText(this, "Image_rate_this_place", Toast.LENGTH_SHORT).show();
        //DataLogger.writeTolog("_________________________________start_a_new_test____________________________"+"\n");
        Intent intent = new Intent(this, RateThisPlaceActivity.class);
        startActivity(intent);

    }



    public void checkFirstRun() {
        boolean DoesUserAgree = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("DoesUserAgree", false);

        if (DoesUserAgree){
            // Place your dialog code here to display the dialog

            Log.i(FirstRun_TAG, "User  agree");

            ReadGoogleAccount();
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
                Log.i(ActionBar_TAG, possibleEmail);
            }
        }

        this.getSharedPreferences("UserInfo", this.MODE_PRIVATE)
                .edit()
                .putString("UserID",possibleEmail)
                .apply();

        return possibleEmail;

    }













}
