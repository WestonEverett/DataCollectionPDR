package com.example.datacollectionpdr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.example.datacollectionpdr.nativedata.MotionSample;
import com.google.android.material.tabs.TabLayout;

public class RecordingActivity extends DataManager {


    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Number[][] PlotSeriesData = {{ 1,2,3,4,5,6,7,8,9,10}, { 3, 4,3,3,33,3,3,3,3,3}, { 43, 44,43,3,1,3,3,43,43,43}, { 2, 2,2,3,1,2,2,2,2,2} };

    private final int graphPoints = 20;
    private SetLengthLongArray motionTimeRecentMeasurements;
    private SetLengthFloatArray[] accRecentMeasurements;
    private SetLengthFloatArray[] gyroRecentMeasurements;
    private SetLengthFloatArray[] rotRecentMeasurements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        initRecentData();

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

    private void initRecentData(){
        motionTimeRecentMeasurements = new SetLengthLongArray(graphPoints);
        accRecentMeasurements = new SetLengthFloatArray[]{
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
        };
        gyroRecentMeasurements = new SetLengthFloatArray[]{
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
        };
        rotRecentMeasurements = new SetLengthFloatArray[]{
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
        };
    }

    @Override
    protected void newCompleteMotionSample(MotionSample motionSample){
        super.newCompleteMotionSample(motionSample);

        motionTimeRecentMeasurements.addValue(motionSample.initTime);

        float[] acc = motionSample.getAcc();

        accRecentMeasurements[0].addValue(acc[0]);
        accRecentMeasurements[1].addValue(acc[1]);
        accRecentMeasurements[2].addValue(acc[2]);

        float[] gyro = motionSample.getGyro();

        gyroRecentMeasurements[0].addValue(gyro[0]);
        gyroRecentMeasurements[1].addValue(gyro[1]);
        gyroRecentMeasurements[2].addValue(gyro[2]);

        float[] rot = motionSample.getRotVector();

        rotRecentMeasurements[0].addValue(rot[0]);
        rotRecentMeasurements[1].addValue(rot[1]);
        rotRecentMeasurements[2].addValue(rot[2]);
    }

    public Number[][] getMyData(String sensor) {

        Long[] times = motionTimeRecentMeasurements.getArray();
        switch (sensor){
            case "Accelerometer":
                Float[] accX = accRecentMeasurements[0].getArray();
                Float[] accY = accRecentMeasurements[1].getArray();
                Float[] accZ = accRecentMeasurements[2].getArray();
                return new Number[][]{times, accX, accY, accZ};
            case "Gyroscope":
                Float[] gyroX = gyroRecentMeasurements[0].getArray();
                Float[] gyroY = gyroRecentMeasurements[1].getArray();
                Float[] gyroZ = gyroRecentMeasurements[2].getArray();
                return new Number[][]{times, gyroX, gyroY, gyroZ};
            case "Rotation":
                Float[] rotX = rotRecentMeasurements[0].getArray();
                Float[] rotY = rotRecentMeasurements[1].getArray();
                Float[] rotZ = rotRecentMeasurements[2].getArray();
                return new Number[][]{times, rotX, rotY, rotZ};
        }

        return new Number[][]{times};
    }

    /** method linking to the help view triggered by a button */
    public void stopRecording(View view){
        Intent intent = new Intent(this, RecordingReview.class);
        startActivity(intent); //Go to the Show Help activity and its view
    }

}