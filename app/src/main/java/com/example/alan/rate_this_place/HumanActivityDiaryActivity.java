package com.example.alan.rate_this_place;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class HumanActivityDiaryActivity extends AppCompatActivity {

    private String[] monthsArray = { "Stop",
                                     "Walking-Onhand","Walking-Onpocket","Walking-Onbag",
                                     "Lift-Up-Onhand","Lift-Up-Onpocket","Lift-Up-Onbag",
                                     "Lift-Down-Onhand","Lift-Down-Onpocket","Lift-Down-Onbag",
                                     "escalator-Up-Onhand","escalator-Up-Onpocket","escalator-Up-Onbag",
                                     "escalator-Down-Onhand","escalator-Down-Onpocket","escalator-Down-Onbag",
                                     "Stair-Up-Onhand","Stai-Up-Onpocket","Stai-Up-Onbag",
                                     "Stair-Down-Onhand","Stai-Down-Onpocket","Stai-Down-Onbag",
                                     "Jogging-Onhand","Jogging-Onpocket","Jogging-Onbag",
                                     "Running-Onhand","Running-Onpocket","Running-Onbag",
                                     "Sitdown-Onhand","Sitdown-Onpocket","Sitdown-Onbag",


              "Bicycle", "MRT", "BUS",   "Working at Office", "Fitness equipment"
            , "Sit down – HP on table / surface"
            , "Sit down – HP in pocket"
            , "Sit down – using HP read news / send msg etc"
             };

    protected static final String HumanActivityTAG = "HumanActivity";
    private ListView HumanActivityListView;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_activity_diary);

        HumanActivityListView = (ListView) findViewById(R.id.listView_HumanActivity);

        // this-The current activity context.
        // Second param is the resource Id for list layout row item
        // Third param is input array
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, monthsArray);
        HumanActivityListView.setAdapter(arrayAdapter);

        HumanActivityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                clickthelistview(a, v, position, id);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_human_activity_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickthelistview(AdapterView<?> a, View v, int position, long id) {

        String value = (String)a.getItemAtPosition(position);
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
        if (value.equals("Stop") ){
            Log.i(HumanActivityTAG, "Stop");

            stopService(new Intent(this, SensorListenerService.class));
            DataLogger.SelfLabel_Human_Status="Stop";

        }else{
            Log.i(HumanActivityTAG, "Others");
            startService(new Intent(this, SensorListenerService.class));
            DataLogger.SelfLabel_Human_Status=value;
        }

      //  DataLogger.writeTolog("________________________________ "+value + "________________________________\n",SensorListenerService.logswich);
        Log.i(HumanActivityTAG, value);

    }
}
