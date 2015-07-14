package com.example.alan.rate_this_place;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

/**
 * A placeholder fragment containing a simple view.
 */
public class RateThisPlace2ActivityFragment extends Fragment {

    public RateThisPlace2ActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        TabHost tabHost = (TabHost)getActivity().findViewById(android.R.id.tabhost);


        tabHost.setup();



        TabHost.TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.linearLayout_basic);
        spec1.setIndicator("Tab 1");

        TabHost.TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setIndicator("Tab 2");
        // spec2.setContent(R.id.linearLayout_detail);
        Intent intentBerry = new Intent().setClass(getActivity(), RateThisPlaceBasicActivity.class);
        spec2.setContent(intentBerry);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        return inflater.inflate(R.layout.activity_rate_this_place, container, false);
    }
}
