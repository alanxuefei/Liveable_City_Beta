package com.example.alan.liveable_city_beta;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Xue Fei on 1/7/2015.
 */

public class UploadFilesToFTPTask extends AsyncTask {
    private Context context;



    public UploadFilesToFTPTask(Context context) {
        super();
        this.context=context;


    }

    @Override
    protected Object doInBackground(Object[] params) {

        connnectingwithFTP();

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

        String ip="ftp.adajinyuanbao.com";
        String userName=       "i2r@adajinyuanbao.com";
        String pass=       "#5BDr+3[J;OS";
        boolean status = false;
        FTPClient mFtpClient = new FTPClient();






        try {


            Log.e("FTP", String.valueOf(status));
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);
            Log.e("FTP", String.valueOf(status));
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
            boolean existing =mFtpClient.changeWorkingDirectory(DataLogger.Myid);
            if (existing) {
                Log.e("FTP", "can");
            }
            else{
                Log.e("FTP", "can not");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = timeformat.format(new Date());

        File myfile = new File(Environment.getExternalStorageDirectory(),  DataLogger.mystorefilename);

        File to = new File(Environment.getExternalStorageDirectory(),  DataLogger.Myid+"_"+timestamp+"_"+DataLogger.mystorefilename);
        myfile.renameTo(to);
        Log.e("FTP", to.getName());
        Log.e("FTP", myfile.getName());

        if (myfile!=null) {

            uploadFile(mFtpClient, to, "");
        }
        else{
            Log.e("FTP",  "can not find file");
        }
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
            Log.e("FTP", "uploading");
            ftpClient.setCopyStreamListener(createListener());
            boolean status = ftpClient.storeFile(serverfilePath+downloadFile.getName(),
                    srcFileStream);
            Log.e("FTP", "done");
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


}