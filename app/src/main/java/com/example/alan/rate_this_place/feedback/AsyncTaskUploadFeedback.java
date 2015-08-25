package com.example.alan.rate_this_place.feedback;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Xue Fei on 1/7/2015.
 */

public class AsyncTaskUploadFeedback extends AsyncTask {
    private Context context;
    String userid,Feedback;




    public AsyncTaskUploadFeedback(Context context,  String feedback0) {
        super();
        this.context=context;
        this.Feedback=feedback0;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userid=context.getSharedPreferences("UserInfo", context.MODE_PRIVATE).getString("UserID","unknown");
    }


    @Override
    protected Object doInBackground(Object[] params) {

        UploadFeedbacktoServer(Feedback);
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Toast.makeText(this.context, "File is uploaded successfully", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        Toast.makeText(this.context, "uploading", Toast.LENGTH_SHORT).show();
    }

    public void UploadFeedbacktoServer(String obj){


        URL url = null;
        try {

            url = new URL("http://www.ratethisplace.co/feedback.php?Feedback="+obj.toString().replaceAll(" ", "%20"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        Log.i("php", url.toString());
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}