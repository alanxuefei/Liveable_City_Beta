package com.example.alan.rate_this_place.mapview;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Xue Fei on 1/7/2015.
 */

public class AsyncTaskGetDataToMap extends AsyncTask {
    private Activity context;
    private String UserID;
    protected static final String GetDataToMap_TAG = "GetDataToMap";
    GoogleMap mMap;
    Location mLastLocation;




    public AsyncTaskGetDataToMap(GoogleMap mmMap,Location mmLastLocation) {
        super();
        this.mMap=mmMap;
        this.mLastLocation= mmLastLocation;



    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();



    }


    @Override
    protected Object doInBackground(Object[] params) {
        Log.i(GetDataToMap_TAG, "start");
        URL url = null;
        try {

            url = new URL("http://www.ratethisplace.co/getDBtoMap.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }



        InputStream in = null;
        try {
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return total.toString();
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.i(GetDataToMap_TAG, o.toString());

        try {
            JSONArray mJsonArray = new JSONArray(o.toString().replace("[],", "").replace("][",","));
            for(int i = 0 ; i < mJsonArray.length(); i++) {
                Log.i(GetDataToMap_TAG, mJsonArray.getJSONObject(i).toString());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble( mJsonArray.getJSONObject(i).getString("LocationLatitude")), Double.parseDouble(mJsonArray.getJSONObject(i).getString("LocationLongitude"))))
                                .title(mJsonArray.getJSONObject(i).getString("Date")+" "+mJsonArray.getJSONObject(i).getString("Time")).snippet(mJsonArray.getJSONObject(i).getString("Comment")).flat(true)).showInfoWindow();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        Toast.makeText(this.context, "uploading", Toast.LENGTH_SHORT).show();
    }




}