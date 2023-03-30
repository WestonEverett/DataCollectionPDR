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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class RecordingActivity extends DataManager {
    private DataViewModel viewModel;
public static double[] currPosCoordinates;
    // Initialize view


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        viewModel = new ViewModelProvider(this).get(DataViewModel.class);
        showProperFragment();
        this.startRecording();
    }

    @Override
    protected void newCompleteMotionSample(MotionSample motionSample){
        super.newCompleteMotionSample(motionSample);

        viewModel.updateSample(motionSample);
    }

    private void showProperFragment() {
        Fragment fragmentToShow = new StartRecFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView_recording_activity, fragmentToShow);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void stopRecording(){
        TrajectoryNative trajectoryNative = this.endRecording();
        try {
            createDataFile(trajectoryNative);
        } catch (IOException e) {
            Log.e("hm", "FAILEDFAILEDFAILEDFAILEDFAILEDFAEILDFAILEDFAILFEFAIELDA");
        }
    }

    private void createDataFile(TrajectoryNative trajectoryNative) throws IOException {
        Context context = getApplicationContext();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dataFileName = "Trajectory_" + timeStamp + ".pkt";
        File file = new File(context.getFilesDir(), dataFileName);

        try (FileOutputStream fos = context.openFileOutput(dataFileName, Context.MODE_PRIVATE)) {
            fos.write(trajectoryNative.generateSerialized().toByteArray());
        }

        Log.e("hm",dataFileName);
    }
}