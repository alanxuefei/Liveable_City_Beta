package com.example.alan.rate_this_place;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

public class RateThisPlaceActivity  extends TabActivity implements TabHost.OnTabChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_place);
        Intent intent = getIntent();
        String From= intent.getStringExtra("From");
        String TheLocation= intent.getStringExtra("TheLocation");

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setIndicator("Simple");
        Intent startBasicIntent = new Intent();
        startBasicIntent.putExtra("From",From);
        startBasicIntent.putExtra("TheLocation", TheLocation);
        spec1.setContent(startBasicIntent.setClass(this, RateThisPlaceBasicActivity.class));

        TabHost.TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setIndicator("Detail");
        Intent startDetailIntent = new Intent();
        startDetailIntent.putExtra("From",From);
        startDetailIntent.putExtra("TheLocation", TheLocation);
        spec2.setContent(startDetailIntent.setClass(this, RateThisPlaceDetailActivity.class));

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rate_this_place, menu);
        return super.onCreateOptionsMenu(menu);

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

    @Override
    public void onTabChanged(String tabId) {

    }

    public void ReturnButton(View v) {
        Log.i("test", "returen");
        super.onBackPressed();

    }





}

