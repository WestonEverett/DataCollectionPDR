package com.example.datacollectionpdr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.google.android.material.tabs.TabLayout;

public class RecordingActivity extends DataManager {


    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Number[][] PlotSeriesData = {{ 1,2,3,4,5,6,7,8,9,10}, { 3, 4,3,3,33,3,3,3,3,3}, { 43, 44,43,3,1,3,3,43,43,43}, { 2, 2,2,3,1,2,2,2,2,2} };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        viewPager = findViewById(R.id.viewpager);

        // setting up the adapter
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // add the fragments
        viewPagerAdapter.add(new MapFragment(), "MAP");
        viewPagerAdapter.add(new GraphsFragment(), "SENSOR DATA");
        viewPagerAdapter.add(new PathFragment(), "TRAJECTORY");

        // Set the adapter
        viewPager.setAdapter(viewPagerAdapter);

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().
        tabLayout = findViewById(R.id.tab_layout);

    }

    public Number[][] getMyData() {

        return PlotSeriesData;
    }

    /** method linking to the help view triggered by a button */
    public void stopRecording(View view){
        Intent intent = new Intent(this, RecordingReview.class);
        startActivity(intent); //Go to the Show Help activity and its view
    }

}