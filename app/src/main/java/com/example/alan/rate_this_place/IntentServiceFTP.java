package com.example.alan.rate_this_place;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServiceFTP extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this


    public IntentServiceFTP() {
        super("IntentServiceFTP");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
         //connnectingwithFTP();

        testphp();
         Log.e("Status", "done");
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


            Log.e("isFTPConnected", String.valueOf(status));
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);
            Log.e("isFTPConnected", String.valueOf(status));
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();
                FTPFile[] mFileArray = mFtpClient.listFiles();
                Log.e("Size",  mFileArray.toString());
            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File thefile = new File(Environment.getExternalStorageDirectory(),  "/" + "RateThisPlace" + "/" + "ActiveData/" + "simplerating.txt");
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = timeformat.format(new Date());
       uploadFile(mFtpClient, thefile, DataLogger.Myid + "_" + timestamp);

    }

    /**
     *
     * @param ftpClient FTPclient object
     * @param downloadFile local file which need to be uploaded.
     */
    public void uploadFile(FTPClient ftpClient, File downloadFile, String serverfilePath) {
        try {
            FileInputStream srcFileStream = new FileInputStream(downloadFile);
            boolean status = ftpClient.storeFile(serverfilePath+downloadFile.getName(),
                    srcFileStream);
            Log.e("Status", String.valueOf(status));
            srcFileStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void testphp() {




        URL url = null;
        try {
            url = new URL("http://www.ratethisplace.co/uploadtoDB.php?Datatime=2011:11:19%2011:11:11" +
                    "&Location=singpoare%2022311" +
                    "&Userid=alan%20%20i2r" +
                    "&Feeling=happ%20%20y" +
                    "&Thi%20%20%20sPla%20%20%20%20ceIs=clean" +
                    "&Comment=Ilike%20%20%20thispl%20%20%20!ace%22");
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
