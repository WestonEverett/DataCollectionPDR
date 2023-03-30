package com.example.datacollectionpdr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.serializationandserver.FileManager;
import com.example.datacollectionpdr.serializationandserver.ServerManager;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class RecordingActivity extends DataManager {

    private DataViewModel viewModel;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_recording);

        viewModel = new ViewModelProvider(this).get(DataViewModel.class);

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
        tabLayout.setupWithViewPager(viewPager);

        this.startRecording();

    }

    @Override
    protected void newCompleteMotionSample(MotionSample motionSample){
        super.newCompleteMotionSample(motionSample);

        viewModel.updateSample(motionSample);
    }

    /** method linking to the help view triggered by a button */
    public void stopRecording(View view){
        Intent intent = new Intent(this, RecordingReview.class);
        TrajectoryNative trajectoryNative = this.endRecording();
        /*
        try {
            FileManager.createDataFile(getApplicationContext(), trajectoryNative);
        } catch (IOException e) {
            Log.e("hm", "FAILEDFAILEDFAILEDFAILEDFAILEDFAEILDFAILEDFAILFEFAIELDA");
        }
        */

        ServerManager serverManager = new ServerManager("temp");

        try {
            serverManager.sendData(trajectoryNative);
        } catch (Exception e){
            Log.e("server error", "Server error: " + String.valueOf(e));
        }

        startActivity(intent); //Go to the Show Help activity and its view
    }

}