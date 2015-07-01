package com.example.alan.liveable_city_beta;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
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
         connnectingwithFTP();

        Log.e("Status", "done");
    }



    /**
     *
     */
    public void connnectingwithFTP() {

        String ip="ftp.adajinyuanbao.com";
        String userName=       "i2r@adajinyuanbao.com";
        String pass=       "#5BDr+3[J;OS";
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
        File thefile = new File(Environment.getExternalStorageDirectory(),  DataLogger.mystorefilename);
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = timeformat.format(new Date());
        uploadFile(mFtpClient,thefile,DataLogger.Myid+"_"+timestamp);

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

}
