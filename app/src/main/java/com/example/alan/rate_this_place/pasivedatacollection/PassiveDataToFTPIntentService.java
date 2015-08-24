package com.example.alan.rate_this_place.pasivedatacollection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.alan.rate_this_place.utility.DataLogger;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PassiveDataToFTPIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this


    public PassiveDataToFTPIntentService() {
        super("IntentServiceFTP");
    }

    String userid;


    @Override
    protected void onHandleIntent(Intent intent) {


        userid=this.getSharedPreferences("UserInfo", this.MODE_PRIVATE).getString("UserID","unknown");

        connnectingwithFTP();
        Log.e("FTP", "done");
    }



    /**
     *
     */
    public void connnectingwithFTP() {

        String ip="ftp.ratethisplace.co";
        String userName="FTP@ratethisplace.co";
        String pass="uMu6Uv+HRqY";
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

        //zip
        SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String datetime  = datetimeformat.format(new Date());
        String INPUT_FOLDER=Environment.getExternalStorageDirectory()+"/"+ "RateThisPlace"+"/PassiveData";
        String ZIPPED_FOLDER=Environment.getExternalStorageDirectory()+"/"+  "RateThisPlace/"+ userid+ datetime+"passivedata.zip";
        zipSimpleFolder(new File(INPUT_FOLDER), "", ZIPPED_FOLDER);

        //enter FTI folder
        // Changing the working directory

        //upload

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
        
        File thefile = new File(ZIPPED_FOLDER);

        if (uploadFile(mFtpClient, thefile, "")){
            DataLogger.EmptyFolder(INPUT_FOLDER);
        };
        thefile.delete();
    }

    /**
     *
     * @param ftpClient FTPclient object
     * @param downloadFile local file which need to be uploaded.
     */
    public boolean uploadFile(FTPClient ftpClient, File downloadFile, String serverfilePath) {

        try {

            FileInputStream srcFileStream = new FileInputStream(downloadFile);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            Log.e("FTP", "uploading ");
            boolean status = ftpClient.storeFile(serverfilePath+downloadFile.getName(),
                    srcFileStream);
            Log.e("FTP", String.valueOf(status));
            srcFileStream.close();
            return status;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void zipSimpleFolder(File inputFolder, String parentName ,String zipFilePath ){

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            String myname = parentName +inputFolder.getName()+"\\";

            ZipEntry folderZipEntry = new ZipEntry(myname);
            zipOutputStream.putNextEntry(folderZipEntry);

            File[] contents = inputFolder.listFiles();

            for (File f : contents){
                if (f.isFile())
                    zipFile(f,myname,zipOutputStream);
            }

            zipOutputStream.closeEntry();
            zipOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void zipFile(File inputFile,String parentName,ZipOutputStream zipOutputStream) {

        try {
            // A ZipEntry represents a file entry in the zip archive
            // We name the ZipEntry after the original file's name
            ZipEntry zipEntry = new ZipEntry(parentName+inputFile.getName());
            zipOutputStream.putNextEntry(zipEntry);

            FileInputStream fileInputStream = new FileInputStream(inputFile);
            byte[] buf = new byte[1024];
            int bytesRead;

            // Read the input file by chucks of 1024 bytes
            // and write the read bytes to the zip stream
            while ((bytesRead = fileInputStream.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, bytesRead);
            }

            // close ZipEntry to store the stream to the file
            zipOutputStream.closeEntry();
            Log.e("FTP", "Regular file :" + inputFile.getCanonicalPath() + " is zipped to archive :");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
