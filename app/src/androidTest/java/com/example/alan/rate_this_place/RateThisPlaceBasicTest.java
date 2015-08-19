package com.example.alan.rate_this_place;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alan.rate_this_place.ratethisplace.RateThisPlaceBasicActivity;


/**
 * Created by Xue Fei on 27/7/2015.
 */
public class RateThisPlaceBasicTest extends ActivityInstrumentationTestCase2<RateThisPlaceBasicActivity> {

private RateThisPlaceBasicActivity mFirstTestActivity;
private TextView mFirstTestText;

public RateThisPlaceBasicTest() {
        super(RateThisPlaceBasicActivity.class);
        }

@Override
protected void setUp() throws Exception {
        super.setUp();
         mFirstTestActivity = getActivity();
         mFirstTestText = (TextView) mFirstTestActivity.findViewById(R.id.textView_locationname);

        }

        public void testdetectinglocation() throws Exception {
                // assertEquals("expected", "actual");

                Log.d("testtag", (String) mFirstTestText.getText());
                assertTrue(("Detecting Location".equals(mFirstTestText.toString())));
                // assertEquals(mFirstTestText, "Detecting Location");


                //assertEquals(mFirstTestText, "Detecting Location");
        }



        public void testName() throws Exception {
               // assertEquals("expected", "actual");


                Thread.sleep(10000);
                 if  ("Detecting Location".equals(mFirstTestText.toString())){
                         Log.d("testtag", "get the location");
                }else{
                         Log.d("testtag", "not get the location");
                 }
                Log.d("testtag", (String) mFirstTestText.getText());
                Log.d("testtag", "my frilesn");
                assertFalse(("Detecting Location".equals(mFirstTestText.toString())));
               // assertEquals(mFirstTestText, "Detecting Location");


                //assertEquals(mFirstTestText, "Detecting Location");
        }


        public void testb() throws Exception {

                // Send string input value
                getInstrumentation().runOnMainSync(new Runnable() {
                        @Override
                        public void run() {
                                EditText senderMessageEditText = (EditText)
                                        mFirstTestActivity.findViewById(R.id.AutoCompleteTextView_Commentary);

                                senderMessageEditText.requestFocus();
                        }
                });
                getInstrumentation().waitForIdleSync();
                getInstrumentation().sendStringSync("Hello Android!");
                getInstrumentation().waitForIdleSync();

        }

        @Override
        protected void tearDown() throws Exception {
                super.tearDown();
        }
}
