package com.example.alan.rate_this_place;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RateThisPlaceBasicActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView actv;
    String[] languages={"This place is not clean","This is a most crowded place on Earth","IOS","SQL","JDBC","Web services"};
    /*google activity detection*/
    protected GoogleApiClient mGoogleApiClient;
    public AddressResultReceiver mResultReceiver = new AddressResultReceiver(this);
    String mAddressOutput;
    private Location mLastLocation;

    private enum Mood {  HAPPY, UNHAPPY }

    private Mood  usermood =Mood.HAPPY;

    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Top Rated", "Games", "Movies" };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_place_basic);


        // Initilization

         /*location */
        // Acquire a reference to the system Location Manager
        // startIntentService();
        Log.i("LoactionName", "User  agree");
        buildGoogleApiClient();



       /* actv = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView_Commentary);
        //String[] countries = getResources().getStringArray(languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,languages);
        actv.setAdapter(adapter);*/

    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("LoactionName", "User  agree1");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
/*googleApi*/
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.rate_this_place_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

         /*location*/



    protected void startLocationNameIntentService(Location location) {


        Log.i("locationname", String.valueOf(mResultReceiver));
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);

    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LoactionName", "GoogleApiClient is connected");
       // mGoogleApiClient.disconnect();
        Log.i("LoactionName", "GoogleApiClient is connected " + mGoogleApiClient.isConnected());

         mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation!=null){
            double longitude = mLastLocation.getLongitude();
            double latitude = mLastLocation.getLatitude();
            String Location_information= "L " + longitude + " " + latitude+" "+mLastLocation.getProvider();
            Log.i("LoactionName", Location_information);
            startLocationNameIntentService(mLastLocation);
            Log.i("LoactionName", "get the location name");
        }
        else{
            Log.i("LoactionName","can not obtain the location name");
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            mGoogleApiClient.reconnect();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }





    class AddressResultReceiver extends ResultReceiver {
        Activity mRateThisPlace;
        public AddressResultReceiver(Activity RateThisPlace) {
            super(null);
            mRateThisPlace=RateThisPlace;

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
             mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //displayAddressOutput(mAddressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
               // showToast(getString(R.string.address_found));
            }
            Log.i("locationname", mAddressOutput);

            mRateThisPlace.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    TextView mEditText_locationname = (TextView) findViewById(R.id.textView_locationname);
                    mEditText_locationname.setText("Location: \n" + mAddressOutput);
                    ProgressBar mprogressBar_locationname = (ProgressBar) findViewById(R.id.progressBar_locationname);
                    mprogressBar_locationname.setVisibility(View.GONE);

                }
            });



        }
    }

    public void clickImage_unhappyface(View view) {


         ((RadioButton)findViewById(R.id.radioButton2)).setChecked(true);;
        usermood =Mood.UNHAPPY;
    }

    public void clickImage_happyface(View view) {



        ((RadioButton)findViewById(R.id.radioButton)).setChecked(true);;
        usermood =Mood.HAPPY;

    }

    public void clickImage_locationname(View view) {

        Log.i("locationname", "click to refresh");
        TextView mEditText_locationname = (TextView) findViewById(R.id.textView_locationname);
        mEditText_locationname.setText("Detecting Location");
        ProgressBar mprogressBar_locationname = (ProgressBar) findViewById(R.id.progressBar_locationname);
        mprogressBar_locationname.setVisibility(View.VISIBLE);
        mGoogleApiClient.reconnect();

    }


    public void clickButton_submit(View view) {

        SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String timestamp = datetimeformat.format(new Date());
        JSONObject JsonGenerator_basicrating = new JSONObject();
        JSONObject JsonGenerator_basicrating_location = new JSONObject();

        try {



            JsonGenerator_basicrating.put("UserID", this.getSharedPreferences("UserInfo", this.MODE_PRIVATE).getString("UserID", null));
            if (mLastLocation==null){JsonGenerator_basicrating_location=null;}
            else {
                JsonGenerator_basicrating_location.put("longitude", mLastLocation.getLongitude());
                JsonGenerator_basicrating_location.put("latitude", mLastLocation.getLatitude());
            }
            JsonGenerator_basicrating.put("Datatime", timestamp);
            JsonGenerator_basicrating.put("Location", JsonGenerator_basicrating_location);
            JsonGenerator_basicrating.put("Feeling", usermood.toString());
            JsonGenerator_basicrating.put("Clean", ((CheckBox) findViewById(R.id.checkBox1)).isChecked());
            JsonGenerator_basicrating.put("Safe", ((CheckBox) findViewById(R.id.checkBox2)).isChecked());
            JsonGenerator_basicrating.put("Green", ((CheckBox) findViewById(R.id.checkBox3)).isChecked());
            JsonGenerator_basicrating.put("Commentary", ((AutoCompleteTextView)findViewById(R.id.AutoCompleteTextView_Commentary)).getText().toString());
            Log.i("JSON", JsonGenerator_basicrating.toString());
            DataLogger.writeSimpleRatingTolog(JsonGenerator_basicrating.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // clickbuttonRecieve();
        Intent mServiceIntent = new Intent(this, IntentServiceFTP.class);
        mServiceIntent.putExtra("this",JsonGenerator_basicrating.toString());
        startService(mServiceIntent);
        Toast.makeText(this, "uploading", Toast.LENGTH_SHORT).show();
      //  AsyncTaskUploadFilesToFTP myfileuploader = new AsyncTaskUploadFilesToFTP(this);
      //  myfileuploader.execute();


    }


    /*
     [
   {
     "datatime": 2015-01-01 21:30:51,
     "comment": "I suggest to add a bin here",
     "location": "latitude longitude",
     "mood": "Happy",
     "Clean":
     "Safe":
     "Green":
     "user": {
       "name": "android_newb",
       "followers_count": 41

   },
   {
     "id": 912345678902,
     "text": "@android_newb just use android.util.JsonWriter!",
     "geo": [50.454722, -104.606667],
     "user": {
       "name": "jesse",
       "followers_count": 2
     }
   }
 ]}
     */


}
