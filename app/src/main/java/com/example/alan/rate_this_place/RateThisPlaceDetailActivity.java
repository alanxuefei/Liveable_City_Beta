package com.example.alan.rate_this_place;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class RateThisPlaceDetailActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView actv;
    String[] languages={"This place is not clean","This is a most crowded place on Earth","IOS","SQL","JDBC","Web services"};
    /*google activity detection*/
    protected GoogleApiClient mGoogleApiClient;
    public AddressResultReceiver mResultReceiver = new AddressResultReceiver(this);
    String mAddressOutput;



    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Top Rated", "Games", "Movies" };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_place_detail);


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

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
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


       /* CheckBox checkBox=(CheckBox)findViewById(R.id.checkBox1);
        CheckBox checkBox1=(CheckBox)findViewById(R.id.checkBox2);
        CheckBox checkBox2=(CheckBox)findViewById(R.id.checkBox3);

        checkBox.setText("Unclean");
        checkBox1.setText("Unsafe");
        checkBox2.setText("Unfriendly");
*/
    }

    public void clickImage_happyface(View view) {


  /*      CheckBox checkBox=(CheckBox)findViewById(R.id.checkBox1);
        CheckBox checkBox1=(CheckBox)findViewById(R.id.checkBox2);
        CheckBox checkBox2=(CheckBox)findViewById(R.id.checkBox3);

        checkBox.setText("Clean");
        checkBox1.setText("Safe");
        checkBox2.setText("Friendly");
*/
    }


    public void clickImage_takeapicture(View view) {


        dispatchTakePictureIntent();

    }



    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView  mImageView_takeapicture = (ImageView) findViewById(R.id.imageView_picture);
            mImageView_takeapicture.setImageBitmap(imageBitmap);
        }
    }

    public void clickImage_locationname(View view) {

        Log.i("locationname", "click to refresh");
        TextView mEditText_locationname = (TextView) findViewById(R.id.textView_locationname);
        mEditText_locationname.setText("Detecting Location");
        ProgressBar mprogressBar_locationname = (ProgressBar) findViewById(R.id.progressBar_locationname);
        mprogressBar_locationname.setVisibility(View.VISIBLE);
        mGoogleApiClient.reconnect();

    }


}
