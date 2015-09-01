package com.example.alan.rate_this_place.mapview;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alan.rate_this_place.MainActivity;
import com.example.alan.rate_this_place.R;
import com.example.alan.rate_this_place.feedback.FeedbackDialogFragment;
import com.example.alan.rate_this_place.myrewards.MyRewardActivity;
import com.example.alan.rate_this_place.pasivedatacollection.PassiveDataToFTPIntentService;
import com.example.alan.rate_this_place.pasivedatacollection.SensorListenerService;
import com.example.alan.rate_this_place.ratethisplace.RateThisPlaceActivity;
import com.example.alan.rate_this_place.usersetting.UserAgreementDialogFragment;
import com.example.alan.rate_this_place.usersetting.UserProfileActivity;
import com.example.alan.rate_this_place.visitedplace.VisitedPlacesActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PopupMenu.OnMenuItemClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected static final String Googlemap_TAG = "Googlemap";
    protected static final String GPS_Internet_Check_TAG = "GPS_Internet_Check";
    protected static final String FirstRun_TAG = "FirstRun";
    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        checkNetworkandGPS();
        checkFirstRun();
        ReadGoogleAccount();
        buildGoogleApiClient();

    }

    @Override
    protected void onResume() {
        super.onResume();
      //  setUpMapIfNeeded();
        mGoogleApiClient.reconnect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
        (new AsyncTaskGetDataToMap(this,mMap,mLastLocation)).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
       // Log.i(Googlemap_TAG, "ready");


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
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation!=null){
            setUpMapIfNeeded();
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

    public void setList(String value){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Menu:
                Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();


                if (isConnectingToInternet()){
                    startActivity(new Intent(this, MainActivity.class));
                }
                else{
                    Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.action_manualupload:
                Toast.makeText(this, "manualupload", Toast.LENGTH_SHORT).show();
                if (isConnectingToInternet()){
                    startService(new Intent(getBaseContext(), PassiveDataToFTPIntentService.class));
                }
                else{
                    Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_visitedplace:
                Toast.makeText(this, "Music Clicked", Toast.LENGTH_SHORT).show();
                clickImage_activity_log();
                return true;
            case R.id.action_myreward:
                Toast.makeText(this, "My Reward", Toast.LENGTH_SHORT).show();
                clickImage_myreward();
                return true;

            case R.id.action_userprofile:
                Toast.makeText(this, "userprofile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;

            case R.id.action_feedback:
                Toast.makeText(this, "Feedback Clicked", Toast.LENGTH_SHORT).show();
                new FeedbackDialogFragment().show(getSupportFragmentManager(), "FeedbackDialog");
                return true;
            case R.id.action_aboutus:
                Toast.makeText(this, "Music Clicked", Toast.LENGTH_SHORT).show();
                return true;

        }
        return true;
    }

    public void clickImage_rate_this_place(View view) {
        // Toast.makeText(this, "Image_rate_this_place", Toast.LENGTH_SHORT).show();
        //DataLogger.writeTolog("_________________________________start_a_new_test____________________________"+"\n");
        Intent intent = new Intent(this, RateThisPlaceActivity.class);
        intent.putExtra("From", "MainActivity");
        startActivity(intent);

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

    public void clickImage_myreward( ) {

        Intent intent = new Intent(this, MyRewardActivity.class);
        startActivity(intent);

    }

    public void clickImage_activity_log() {

        Intent intent = new Intent(this, VisitedPlacesActivity.class);
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
    public void checkNetworkandGPS()
    {

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Log.i(GPS_Internet_Check_TAG, "GPS Yes");
            if (isConnectingToInternet())
            {
                ((TextView)findViewById(R.id.textView_status)).setVisibility(View.GONE);
                Log.i(GPS_Internet_Check_TAG, "internet Yes");

            }
            else{
                ((TextView)findViewById(R.id.textView_status)).setText("Internet is not available");

                Log.i(GPS_Internet_Check_TAG, "internet No");
            }
        }
        else{

            Log.i(GPS_Internet_Check_TAG, "GPS No");
            if (isConnectingToInternet())
            {
                ((TextView)findViewById(R.id.textView_status)).setText("GPS is off");
                Log.i(GPS_Internet_Check_TAG, "internet Yes");
            }
            else{
                ((TextView)findViewById(R.id.textView_status)).setText("Internet is not available and GPS is off");

                Log.i(GPS_Internet_Check_TAG, "internet No");
            }

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

        //((TextView)findViewById(R.id.textView_UserID)).setText("UserID: "+possibleEmail);
        return possibleEmail;

    }




}
