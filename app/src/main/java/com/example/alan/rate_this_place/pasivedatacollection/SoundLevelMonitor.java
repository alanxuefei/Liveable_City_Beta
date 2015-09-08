package com.example.alan.rate_this_place.pasivedatacollection;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by Xue Fei on 25/5/2015.
 */
public class SoundLevelMonitor   {
    /*audio*/
    public MediaRecorder mRecorder = null;


    /*Audio*/
    public void Soundlevel_start() {

           if (mRecorder == null) {
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.setOutputFile("/dev/null");
                try {
                    mRecorder.prepare();
                    mRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }



    }

    public void Soundlevel_stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double Soundlevel_getAmplitude() {
        if (mRecorder != null)
            return  mRecorder.getMaxAmplitude();
        else
            return 0;

    }
}
