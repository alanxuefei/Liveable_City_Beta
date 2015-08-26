package com.example.alan.rate_this_place.ratethisplace;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.alan.rate_this_place.R;
import com.example.alan.rate_this_place.utility.Constants;
import com.example.alan.rate_this_place.utility.DataLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RateThisPlaceDetailActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView actv;
    String[] languages={"This place is not clean","This is a most crowded place on Earth","IOS","SQL","JDBC","Web services"};
    /*google activity detection*/
    protected GoogleApiClient mGoogleApiClient;
    public AddressResultReceiver mResultReceiver = new AddressResultReceiver(this);
    String mAddressOutput;
    Location mLastLocation= new Location("");

    private enum Mood {NOFEELING, HAPPY, UNHAPPY, SURPRISE,FUNNY,ANGRY,DISLIKE};
    private Mood  usermood =Mood.NOFEELING;



    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Top Rated", "Games", "Movies" };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_place_detail);

        Intent intent = getIntent();

        String From= intent.getStringExtra("From");
        if (From!=null){
            Log.i("LoactionName", From);
            if (From.equals("MainActivity")){
                buildGoogleApiClient();
            }
            else{
                if (From.equals("VisitedPlacesActivity")){
                    String TheLocation= intent.getStringExtra("TheLocation");
                    TextView mEditText_locationname = (TextView) findViewById(R.id.textView_locationname);
                    mEditText_locationname.setText("Location: \n" + TheLocation);
                    LatLng thelocation= Constants.BAY_AREA_LANDMARKS.get(TheLocation);
                    Log.i("LoactionName", thelocation.toString());
                    mLastLocation.setLatitude(thelocation.latitude);//your coords of course
                    mLastLocation.setLongitude(thelocation.longitude);
                    ProgressBar mprogressBar_locationname = (ProgressBar) findViewById(R.id.progressBar_locationname);
                    mprogressBar_locationname.setVisibility(View.GONE);

                }
            }
        }
        addListenerOnRatingBar();
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
    protected void onDestroy() {
        super.onDestroy();
        ((ImageView) findViewById(R.id.imageView_picture)).setImageBitmap(null);
        //  Intent intent = new Intent(this, SensorListenerService.class);
        // stopService(intent);
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



    }

    public void clickImage_happyface(View view) {


    }


    public void clickImage_takeapicture(View view) {


        dispatchTakePictureIntent();

    }


    static final int REQUEST_IMAGE_CAPTURE = 1;


    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = this.getSharedPreferences("UserInfo", this.MODE_PRIVATE).getString("UserID", null)
                             + "_"+timeStamp + "_Lat_"+mLastLocation.getLatitude()+"_Lon_"+mLastLocation.getLongitude()+"_"+mLastLocation.getProvider()+"_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/" + "RateThisPlace" + "/" + "ActiveData/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(photoFile.toString());
          //  Bundle extras = data.getExtras();
          //  Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((ImageView) findViewById(R.id.imageView_picture)).setImageURI(Uri.fromFile(file));
           // ((ImageView) findViewById(R.id.imageView_picture)).setImageBitmap(imageBitmap);
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

    public void clickimage_happyface(View view) {
        ((RadioButton)findViewById(R.id.radioButton1)).setChecked(true);;
        clickRadio_group1(view);
        usermood =Mood.HAPPY;
        ((TextView)findViewById(R.id.textView)).setText("This place makes me feel: Happy");;


    }
    public void clickimage_unhappyface(View view) {
        ((RadioButton)findViewById(R.id.radioButton2)).setChecked(true);;
        clickRadio_group1(view);
        usermood =Mood.UNHAPPY;
        ((TextView)findViewById(R.id.textView)).setText("This place makes me feel: Unhappy");;
    }
    public void clickimage_surprisedface(View view) {
        ((RadioButton)findViewById(R.id.radioButton3)).setChecked(true);;
        clickRadio_group2(view);
        usermood =Mood.SURPRISE;
        ((TextView)findViewById(R.id.textView)).setText("This place makes me feel: Surprise");;
    }
    public void clickimage_funnyface(View view) {
        ((RadioButton)findViewById(R.id.radioButton4)).setChecked(true);;
        clickRadio_group2(view);
        usermood =Mood.FUNNY;
        ((TextView)findViewById(R.id.textView)).setText("This place makes me feel: Funny");;
    }
    public void clickimage_angryface(View view) {
        ((RadioButton)findViewById(R.id.radioButton5)).setChecked(true);;
        clickRadio_group3(view);
        usermood =Mood.ANGRY;
        ((TextView)findViewById(R.id.textView)).setText("This place makes me feel: Angry");;
    }
    public void clickimage_dislikeface(View view) {
        ((RadioButton)findViewById(R.id.radioButton6)).setChecked(true);;
        clickRadio_group3(view);
        usermood =Mood.DISLIKE;
        ((TextView)findViewById(R.id.textView)).setText("This place makes me feel: Dislike");;
    }


    public void clickRadio_group1(View view) {

        ((RadioGroup)findViewById(R.id.radioGroup2)).clearCheck();
        ((RadioGroup)findViewById(R.id.radioGroup3)).clearCheck();
    }
    public void clickRadio_group2(View view) {

        ((RadioGroup)findViewById(R.id.radioGroup1)).clearCheck();
        ((RadioGroup)findViewById(R.id.radioGroup3)).clearCheck();

    }
    public void clickRadio_group3(View view) {

        ((RadioGroup)findViewById(R.id.radioGroup2)).clearCheck();
        ((RadioGroup)findViewById(R.id.radioGroup1)).clearCheck();
    }

    public void addListenerOnRatingBar() {

        final String[] ratingscale = {"Very poor","Poor", "Average", "Good","Excellent"};

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarCLEANNESS);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {

                TextView mtextViewCLEANNESS = (TextView) findViewById(R.id.textViewCLEANNESS);
                mtextViewCLEANNESS.setText(ratingscale[(int)rating-1]);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBarSAFTY);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {

                TextView mtextViewCLEANNESS = (TextView) findViewById(R.id.textViewSAFTY);
                mtextViewCLEANNESS.setText(ratingscale[(int)rating-1]);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBarBEAUTIFULNESS);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {

                TextView mtextViewCLEANNESS = (TextView) findViewById(R.id.textViewBEAUTIFULNESS);
                mtextViewCLEANNESS.setText(ratingscale[(int)rating-1]);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBarGREENNESS);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {

                TextView mtextViewCLEANNESS = (TextView) findViewById(R.id.textViewGREENNESS);
                mtextViewCLEANNESS.setText(ratingscale[(int)rating-1]);
            }
        });


        ratingBar = (RatingBar) findViewById(R.id.ratingBarFRIENDLINESS);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {

                TextView mtextViewCLEANNESS = (TextView) findViewById(R.id.textViewFRIENDLINESS);
                mtextViewCLEANNESS.setText(ratingscale[(int)rating-1]);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBarCONVENIENCE);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {

                TextView mtextViewCLEANNESS = (TextView) findViewById(R.id.textViewCONVENIENCE);
                mtextViewCLEANNESS.setText(ratingscale[(int)rating-1]);
            }
        });

    }

    public void clickButton_submit(View view) {

        SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = datetimeformat.format(new Date());
        JSONObject JsonGenerator_basicrating = new JSONObject();
        JSONObject JsonGenerator_basicrating_location = new JSONObject();

        try {
            JsonGenerator_basicrating.put("UserID", this.getSharedPreferences("UserInfo", this.MODE_PRIVATE).getString("UserID", null));
            JsonGenerator_basicrating.put("Nickname",   PreferenceManager.getDefaultSharedPreferences(this).getString("display_name", ""));
            if (mLastLocation==null){JsonGenerator_basicrating_location=null;}
            else {
                JsonGenerator_basicrating_location.put("longitude", mLastLocation.getLongitude());
                JsonGenerator_basicrating_location.put("latitude", mLastLocation.getLatitude());
            }
            JsonGenerator_basicrating.put("Datatime", timestamp);
            JsonGenerator_basicrating.put("Location", JsonGenerator_basicrating_location);
            JsonGenerator_basicrating.put("Feeling", usermood.toString());
            JsonGenerator_basicrating.put("Cleanness", ((RatingBar) findViewById(R.id.ratingBarCLEANNESS)).getRating());
            JsonGenerator_basicrating.put("Safty", ((RatingBar) findViewById(R.id.ratingBarSAFTY)).getRating());
            JsonGenerator_basicrating.put("Beauty", ((RatingBar) findViewById(R.id.ratingBarBEAUTIFULNESS)).getRating());
            JsonGenerator_basicrating.put("Greenness", ((RatingBar) findViewById(R.id.ratingBarGREENNESS)).getRating());
            JsonGenerator_basicrating.put("Friendliness", ((RatingBar) findViewById(R.id.ratingBarFRIENDLINESS)).getRating());
            JsonGenerator_basicrating.put("Convenience", ((RatingBar) findViewById(R.id.ratingBarCONVENIENCE)).getRating());
            JsonGenerator_basicrating.put("Commentary", ((AutoCompleteTextView)findViewById(R.id.AutoCompleteTextView_Commentary)).getText().toString());
            if (photoFile!= null) {JsonGenerator_basicrating.put("PhotoFileName",  photoFile.getName());}
            Log.i("JSON", JsonGenerator_basicrating.toString());
            DataLogger.writeSimpleRatingTolog(JsonGenerator_basicrating.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AsyncTaskUploadDetaledRating myfileuploader = new AsyncTaskUploadDetaledRating(this,JsonGenerator_basicrating,photoFile);
        myfileuploader.execute();

    }


}
