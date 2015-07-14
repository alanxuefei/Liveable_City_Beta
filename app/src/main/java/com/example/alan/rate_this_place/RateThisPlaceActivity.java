package com.example.alan.rate_this_place;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class RateThisPlaceActivity  extends TabActivity implements TabHost.OnTabChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_place);

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        tabHost.setup();



        TabHost.TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.linearLayout_basic);
        spec1.setIndicator("Tab 1");

        TabHost.TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setIndicator("Tab 2");
       // spec2.setContent(R.id.linearLayout_detail);
          Intent intentBerry = new Intent().setClass(this, RateThisPlaceBasicActivity.class);
        spec2.setContent(intentBerry);

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
}
