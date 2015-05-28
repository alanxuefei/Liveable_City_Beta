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

    public static void writeTolog(String content){

        File file;
        FileOutputStream outputStream;

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = s.format(new Date());
        content = timestamp+" "+content;

        try {

            outputStream = new FileOutputStream(file,true);
            outputStream.write(content.getBytes());
            outputStream.close();
            //Log.i("MyActivity", file.toString() + content + "  file done");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
