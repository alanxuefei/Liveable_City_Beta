package com.example.alan.rate_this_place.ratethisplace;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by Xue Fei on 1/7/2015.
 */

public class AsyncTaskUploadDetaledRating extends AsyncTask {
    private Context context;
    String userid;
    protected static final String FTP_TAG = "FTP";
    JSONObject JsonGenerator_basicrating;
    File photoFile;



    public AsyncTaskUploadDetaledRating(Context context, JSONObject JsonGenerator_basicrating0,File photoFile0) {
        super();
        this.context=context;
        this.JsonGenerator_basicrating=JsonGenerator_basicrating0;
        this.photoFile=photoFile0;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        userid=context.getSharedPreferences("UserInfo", context.MODE_PRIVATE).getString("UserID","unknown");
    }


    @Override
    protected Object doInBackground(Object[] params) {


        connnectingwithFTP();
        UploadDetailedRatingtoServer(JsonGenerator_basicrating);
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




    /**
     *
     */
    public void connnectingwithFTP() {

        String ip="ftp.ratethisplace.co";
        String userName=       "FTP@ratethisplace.co";
        String pass=       "uMu6Uv+HRqY";
        boolean status = false;
        FTPClient mFtpClient = new FTPClient();



        try {

            Log.e(FTP_TAG, String.valueOf(status));
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);
            Log.e(FTP_TAG, String.valueOf(status));
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();
                FTPFile[] mFileArray = mFtpClient.listFiles();
                Log.e("FTP",  mFileArray.toString());
            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            if (mFtpClient.changeWorkingDirectory("/"+userid)){
            }
            else{
                mFtpClient.makeDirectory(userid);
                mFtpClient.changeWorkingDirectory("/" + userid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadFile(mFtpClient,  photoFile, "");


    }

    /**
     *
     * @param ftpClient FTPclient object
     * @param downloadFile local file which need to be uploaded.
     */
    public void uploadFile(FTPClient ftpClient, File downloadFile, String serverfilePath) {

        try {

            FileInputStream srcFileStream = new FileInputStream(downloadFile);
          //e5  Toast.makeText(get, "fpt", Toast.LENGTH_SHORT).show();

           ftpClient.setFileType(FTP.BINARY_FILE_TYPE);  // Ascii vs. Binary Files  Zip is Binary File.

           // ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            Log.e("FTP", "uploading ");
          //  ftpClient.setCopyStreamListener(createListener());

            boolean status = ftpClient.storeFile( downloadFile.getName(),
                    srcFileStream);
            Log.e("FTP", "done "+status);
            srcFileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CopyStreamListener createListener(){
        return new CopyStreamListener(){
            private long megsTotal = 0;
            //            @Override
            public void bytesTransferred(CopyStreamEvent event) {
                bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
            }

            //            @Override
            public void bytesTransferred(long totalBytesTransferred,
                                         int bytesTransferred, long streamSize) {


                long megs = totalBytesTransferred / 1000000;
                for (long l = megsTotal; l < megs; l++) {
                    //System.err.print("#");
                    Log.e("FTP", "#");

                }

                megsTotal = megs;
            }
        };
    }

    public void UploadDetailedRatingtoServer(JSONObject obj){


        URL url = null;
        try {

            url = new URL("http://www.ratethisplace.co/uploadDetailedRatingtoDB.php?DetailedRatingJson="+obj.toString().replaceAll(" ", "%20"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        Log.i("php",  url.toString());
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