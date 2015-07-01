package com.example.alan.liveable_city_beta;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by Xue Fei on 25/5/2015.
 */
public class DataLogger {

    /* write into text file*/
    protected static final String Log_TAG = "Log";

    public static String Myid="alan";
    public static String mystorefilename;


    public static void writeTolog(String content,String logswich)  {

        File file;
        FileOutputStream outputStream=null;

        //SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String timestamp = timeformat.format(new Date());
        String datestamp = dateformat.format(new Date());
        content = timestamp+" "+content;
        mystorefilename= Myid+"_"+datestamp+logswich+".txt";


        try {
            file = new File(Environment.getExternalStorageDirectory(),  mystorefilename);

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




}
