package com.example.alan.rate_this_place.ratethisplace;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SimpleRatingToServerIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this


    public SimpleRatingToServerIntentService() {
        super("SimpleRatingToServerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String jsonString = intent.getStringExtra("this");
        JSONObject obj = null;
        try {
             obj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (obj!=null){
            try {
                UploadSimpleRatingtoServer(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

         Log.e("php", "done");
    }


    public void UploadSimpleRatingtoServer(JSONObject obj) throws JSONException {



        URL url = null;
        try {

          url = new URL("http://www.ratethisplace.co/uploadSimpleRatingtoDB.php?SimpleRatingJson="+obj.toString().replaceAll(" ", "%20"));
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
        Log.i("php",  total.toString());

    }

}
