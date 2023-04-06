package com.example.datacollectionpdr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;
import com.example.datacollectionpdr.nativedata.PressureData;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.UserPositionData;
import com.example.datacollectionpdr.serializationandserver.FileManager;
import com.example.datacollectionpdr.serializationandserver.ServerManager;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class RecordingActivity extends DataManager {
    private DataViewModel viewModel;
    public static double[] endCoordinates= {55.988740420441346,-3.241165615618229,0,0};
    public static double[] startCoordinates= {55.988740420441346,-3.241165615618229,0,0};
    public static double[] currPointCoordinates= {0,0};

    public TrajectoryNative trajectoryNative;
    public ArrayList<PDRStep> oldPdrSteps;
    // Initialize view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_recording);
        viewModel = new ViewModelProvider(this).get(DataViewModel.class);
        showProperFragment();
        this.startRecording(new UserPositionData(0,0,1,1));

    }

    @Override
    protected void newCompleteMotionSample(MotionSample motionSample){
        super.newCompleteMotionSample(motionSample);

        viewModel.updateMotionSample(motionSample);
    }

    @Override
    protected void newPDRStep(PDRStep pdrStep){
        super.newPDRStep(pdrStep);

        viewModel.updatePDRSample(pdrStep);
    }

    @Override
    public void onBarometerValueUpdated(float pressure){
        super.onBarometerValueUpdated(pressure);

        viewModel.updatePressure(new PressureData(System.currentTimeMillis(), pressure));
    }

    private void showProperFragment() {
        Fragment fragmentToShow = new StartRecFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView_recording_activity, fragmentToShow);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void stopRecording(){
        trajectoryNative = this.endRecording();
    }
}
