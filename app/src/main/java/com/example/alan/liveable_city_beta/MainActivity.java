package com.example.alan.liveable_city_beta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Xue Fei on 19/5/2015.
 */

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, SensorListenerService.class);
        startService(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SensorListenerService.class);
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(this, SensorListenerService.class);
        startService(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(this, SensorListenerService.class);
       // stopService(intent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this, SensorListenerService.class);
       // stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(this, SensorListenerService.class);
       // stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
