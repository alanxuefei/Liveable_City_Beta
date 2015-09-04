package com.example.alan.rate_this_place.visitedplace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.alan.rate_this_place.R;
import com.example.alan.rate_this_place.ratethisplace.RateThisPlaceActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class VisitedPlacesActivity extends AppCompatActivity {

    protected static final String HumanActivityTAG = "HumanActivity";

   // JSONArray mJsonArray = new JSONArray(o.toString().replace("[],",""));

    List<String> VisitedPlaceList = new ArrayList<>();

    private ListView HumanActivityListView;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_places);

        String content = null;
        try {
            File f=new File(Environment.getExternalStorageDirectory() + "/" + "RateThisPlace" + "/" + "ActiveData/" + "visitedplace.txt");
            if (f.exists()) {
                content = new Scanner(new File(Environment.getExternalStorageDirectory() + "/" + "RateThisPlace" + "/" + "ActiveData/" + "visitedplace.txt")).useDelimiter("\\Z").next();

                JSONArray mJsonArray = new JSONArray( "["+content.toString()+"]");

                for(int i = 0 ; i < mJsonArray.length()-1; i++) {
                    VisitedPlaceList.add(((mJsonArray.getJSONObject(i)).getString("Datetime")).toString()+"\n "+mJsonArray.getJSONObject(i).getString("Geofence").toString());
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
        e.printStackTrace();
        Log.i("visitedpalce", "wrong");
        }






        HumanActivityListView = (ListView) findViewById(R.id.listView_HumanActivity);

        // this-The current activity context.
        // Second param is the resource Id for list layout row item
        // Third param is input array
        arrayAdapter = new ArrayAdapter(this, R.layout.visitedplaces_list_eachrow, R.id.thetheTextView, VisitedPlaceList.toArray());
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
        Log.i(HumanActivityTAG, value);

        Intent startRatingIntent = new Intent(this, RateThisPlaceActivity.class);
        startRatingIntent.putExtra("From","VisitedPlacesActivity");
        startRatingIntent.putExtra("TheLocation", value.split(" ")[1]);
        startActivity(startRatingIntent);


    }

    public void ReturnButton(View v) {
        Log.i("test", "returen");
        super.onBackPressed();
    }
}
