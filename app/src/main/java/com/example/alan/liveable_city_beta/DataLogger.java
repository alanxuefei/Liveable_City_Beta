package com.example.alan.liveable_city_beta;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Xue Fei on 25/5/2015.
 */
public class DataLogger {

    /* write into text file*/

    public static void writeTolog(String content){

        File file;
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStorageDirectory(), "dataset_battery.txt");

            outputStream = new FileOutputStream(file,true);
            outputStream.write(content.getBytes());
            outputStream.close();
            Log.i("MyActivity", file.toString() + content + "  file done");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
