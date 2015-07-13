package com.example.alan.rate_this_place;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class RateThisPlaceActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView actv;
    String[] languages={"This place is not clean","This is a most crowded place on Earth","IOS","SQL","JDBC","Web services"};
    /*google activity detection*/
    protected GoogleApiClient mGoogleApiClient;
    public AddressResultReceiver mResultReceiver = new AddressResultReceiver(this);
    String mAddressOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_place);
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
        getMenuInflater().inflate(R.menu.menu_rate_this_place, menu);
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
        Log.i("LoactionName", "User  agree3");
       // mGoogleApiClient.disconnect();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        double longitude = mLastLocation.getLongitude();
        double latitude = mLastLocation.getLatitude();
        String Location_information= "L " + longitude + " " + latitude+" "+mLastLocation.getProvider();
        Log.i("LoactionName", Location_information);
        startLocationNameIntentService(mLastLocation);


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
                    EditText mEditText_locationname = (EditText) findViewById(R.id.editText_locationname);
                    mEditText_locationname.setText("LOCATION: "+mAddressOutput+" (Tap to change the current location)");
                   // TextView mTextview_locationname = (TextView) findViewById(R.id.textView_locationname);
                   // mTextview_locationname.setText(mAddressOutput+" (Tap to change the current location)");
                    ProgressBar mprogressBar_locationname = (ProgressBar) findViewById(R.id.progressBar_locationname);
                    mprogressBar_locationname.setVisibility(View.GONE);

                }
            });



        }
    }
}
