package com.example.alan.rate_this_place.mapview;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener,LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected static final String Googlemap_TAG = "Googlemap";
    protected static final String GPS_Internet_Check_TAG = "GPS_Internet_Check";
    protected static final String FirstRun_TAG = "FirstRun";
    private LocationManager mlocationManager=null;
    private Marker mylocationmark;

    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkNetworkandGPS();
        checkFirstRun();
        ReadGoogleAccount();
             /*location */
        // Acquire a reference to the system Location Manager
        mlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        mlocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this); //long minTime, float minDistance
        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        setUpMapIfNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();
        //mMap.clear();
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
        (new AsyncTaskGetDataToMap(this,mMap,mLastLocation)).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
       // Log.i(Googlemap_TAG, "ready");


    }



    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_mapview, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Menu:
               // Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();


                if (isConnectingToInternet()){
                    startActivity(new Intent(this, MainActivity.class));
                }
                else{
                   // Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.action_manualupload:
               // Toast.makeText(this, "manualupload", Toast.LENGTH_SHORT).show();
                if (isConnectingToInternet()){
                    startService(new Intent(getBaseContext(), PassiveDataToFTPIntentService.class));
                }
                else{
                    //Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_visitedplace:
               // Toast.makeText(this, "Music Clicked", Toast.LENGTH_SHORT).show();
                clickImage_activity_log();
                return true;
            case R.id.action_myreward:
                //Toast.makeText(this, "My Reward", Toast.LENGTH_SHORT).show();
                clickImage_myreward();
                return true;

            case R.id.action_userprofile:
                //Toast.makeText(this, "userprofile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;

            case R.id.action_feedback:
               // Toast.makeText(this, "Feedback Clicked", Toast.LENGTH_SHORT).show();
                new FeedbackDialogFragment().show(getSupportFragmentManager(), "FeedbackDialog");
                return true;
            case R.id.action_aboutus:
                //Toast.makeText(this, "Music Clicked", Toast.LENGTH_SHORT).show();
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



     /*location*/

    @Override
    public void onLocationChanged(Location location) {


        double longitude = location.getLongitude();
        double latitude =  location.getLatitude();
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        String Location_information= "L " + longitude + " " + latitude+" "+location.getProvider();
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 18));
        if (mylocationmark!=null) mylocationmark.remove();
        mylocationmark=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation))
                .position(loc));



        if(mMap != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f));
        }

        Log.i("location", Location_information);


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


}
