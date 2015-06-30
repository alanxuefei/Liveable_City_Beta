package com.example.alan.liveable_city_beta;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by Xue Fei on 25/5/2015.
 */
public class DataLogger {

    /* write into text file*/
    protected static final String Log_TAG = "Log";



    public static void writeTolog(String content,String logswich)  {

        File file;
        FileOutputStream outputStream=null;

        //SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String timestamp = timeformat.format(new Date());
        String datestamp = dateformat.format(new Date());
        content = timestamp+" "+content;



        try {
            file = new File(Environment.getExternalStorageDirectory(),  "xiaomi"+datestamp+logswich+".txt");

            outputStream = new FileOutputStream(file,true);
            outputStream.write(content.getBytes());

          //  Log.i("Log", content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if ( outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     *
     * @param ip
     * @param userName
     * @param pass
     */
    public static void connnectingwithFTP(String ip, String userName, String pass) {
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
                Log.e("Size", String.valueOf(mFileArray.length));
            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File thefile = new File(Environment.getExternalStorageDirectory(),  "IMG_20150617_174915.jpg");
        uploadFile(mFtpClient,thefile,"d");
    }

    /**
     *
     * @param ftpClient FTPclient object
     * @param downloadFile local file which need to be uploaded.
     */
    public static void uploadFile(FTPClient ftpClient, File downloadFile,String serverfilePath) {
        try {
            FileInputStream srcFileStream = new FileInputStream(downloadFile);
            boolean status = ftpClient.storeFile(downloadFile.getName(),
                    srcFileStream);
            Log.e("Status", String.valueOf(status));
            srcFileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
