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

    private Number[] series1Numbers = {100, 100, 102, 108, 14, 12, 18, 72, 36, 74};


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

    public Number[] getMyData() {
        return series1Numbers;
    }

    /** method linking to the help view triggered by a button */
    public void stopRecording(View view){
        Intent intent = new Intent(this, RecordingReview.class);
        startActivity(intent); //Go to the Show Help activity and its view
    }

}