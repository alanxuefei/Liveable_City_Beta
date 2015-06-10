package com.example.alan.liveable_city_beta;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;


public class RateThisPlaceActivity extends AppCompatActivity implements LocationListener {

    private AutoCompleteTextView actv;
    String[] languages={"This place is not clean","This is a most crowded place on Earth","IOS","SQL","JDBC","Web services"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_this_place);
         /*location */
        // Acquire a reference to the system Location Manager

        actv = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView_Commentary);
        //String[] countries = getResources().getStringArray(languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,languages);
        actv.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rate_this_place, menu);
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

         /*location*/

    @Override
    public void onLocationChanged(Location location) {


        double longitude = location.getLongitude();
        double latitude =  location.getLatitude();
        String Location_information= "L " + longitude + " " + latitude+" "+location.getProvider();

        Toast.makeText(this, Location_information, Toast.LENGTH_SHORT).show();

    }



}
