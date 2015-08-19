package com.example.alan.rate_this_place;

import android.app.Activity;
import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.widget.Spinner;

import com.example.alan.rate_this_place.ratethisplace.RateThisPlaceBasicActivity;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private static final String TAG = "FirstAndroidTest";

    private Activity mActivity; // MyActivity is the class name of the app under test
    private Spinner mSpinner;
    private RateThisPlaceBasicActivity mFirstTestActivity;

    public ApplicationTest() {
        super(Application.class);
    }

    protected void setUp() throws Exception {
        Log.d(TAG, "Running testA");
        createApplication();



    }
    public void testName() throws Exception {
       Log.d(TAG, "Running testA");
        assert false;
    }


    protected void  tearDown()  throws Exception {

    }

}