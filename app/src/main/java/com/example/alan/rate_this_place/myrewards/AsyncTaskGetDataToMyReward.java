package com.example.alan.rate_this_place.myrewards;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

public class AsyncTaskGetDataToMyReward extends AsyncTask {
    private Activity context;
    private String UserID;
    protected static final String AsyncTaskGetDataToMyReward_TAG = "AsyncTaskGetData_MYREWARDS";
    JSONObject obj;
    TextView TextViewReward, TextViewConnection;
    ProgressBar mprogressBar_locationname,progressBar_points;




    public AsyncTaskGetDataToMyReward(JSONObject JsonGenerator_basicrating0, TextView TextViewReward0, TextView textView10,ProgressBar mprogressBar_locationname0) {
        super();
        this.obj=JsonGenerator_basicrating0;
        this.TextViewReward=TextViewReward0;
        this.TextViewConnection=textView10;
        this.mprogressBar_locationname=mprogressBar_locationname0;
       // this.progressBar_points=progressBar_points0;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();



    }


    @Override
    protected Object doInBackground(Object[] params) {
        Log.i(AsyncTaskGetDataToMyReward_TAG, "start");
        URL url = null;


        try {

            url = new URL("http://www.ratethisplace.co/getMyRewards.php?MyRewardsJson="+obj.toString().replaceAll(" ", "%20"));
            Log.i(AsyncTaskGetDataToMyReward_TAG, "http://www.ratethisplace.co/getMyRewards.php?MyRewardsJson="+obj.toString().replaceAll(" ", "%20"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (urlConnection!=null) {

            InputStream in = null;
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (in!=null) {
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
        }
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
       // Log.i(AsyncTaskGetDataToMyReward_TAG, o.toString());
        if (o!=null){


            try {
                JSONObject mJsonResponse = new JSONObject(o.toString().replace("[],",""));
                mJsonResponse.getString("Reward");
                TextViewReward.setText(mJsonResponse.getString("Reward") + " points");
                TextViewConnection.setText("");
                mprogressBar_locationname.setVisibility(View.GONE);
              //  progressBar_points.setProgress(Integer.parseInt(mJsonResponse.getString("Reward")));
              //  Log.i(AsyncTaskGetDataToMyReward_TAG, mJsonResponse.getString("Reward"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            TextViewConnection.setText("Internet is not available");

        }


    }




    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        Toast.makeText(this.context, "Connecting to The Server", Toast.LENGTH_SHORT).show();

    }




}