package com.example.datacollectionpdr;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;
import com.example.datacollectionpdr.nativedata.PressureData;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.UserPositionData;
import java.util.Objects;

/**
 * Class with activity handling all process of data recording and reviewing
 *
 *  * Author: Dagna Wojciak, Weston Everett
 *  * Affiliation: The University of Edinburgh
 */
public class RecordingActivity extends DataManager {

    private DataViewModel viewModel;    //initialise DataViewModel object which gives access to sensor measurements
    public static double[] endCoordinates= {55.988740420441346,-3.241165615618229,0,0}; //initialise user initial position and orientation coordinates
    public static double[] startCoordinates= {55.988740420441346,-3.241165615618229,0,0};//initialise user final position and orientation coordinates
    public static double[] currPointCoordinates= {0,0}; //initialise coordinates of a map marker to be placed by the user
    public TrajectoryNative trajectoryNative;  //initialise trajectory Native object which is used to save data in a server appropriate way

    /**
     * actions to e done when activity is started  - layout and data collection set up
     * @param savedInstanceState - previous state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();   //hide the app bar

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //set up threading for data collection
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_recording);        //set layout from activity_recording xml file

        viewModel = new ViewModelProvider(this).get(DataViewModel.class); //initialise activity view
        showProperFragment();   //set up fragment view

        this.startRecording(new UserPositionData(0,0,1,1)); //start recording (so that gps can be accessed)
    }

    /**
     * When all sensors in motion sample required for the server updated
     * @param motionSample object with all the required sensor data
     */
    @Override
    protected void newCompleteMotionSample(MotionSample motionSample){
        super.newCompleteMotionSample(motionSample);

        viewModel.updateMotionSample(motionSample); //update the motion sample with new measurements
    }

    /**
     * Deal with a new pdr step when new step detected
     * @param pdrStep - object holding pdr values (changes in position)
     */
    @Override
    protected void newPDRStep(PDRStep pdrStep){
        super.newPDRStep(pdrStep);

        viewModel.updatePDRSample(pdrStep);//update pdr with new measurements
    }

    /**
     * Deal with a new pressure measurement
     * @param pressure - object holding barometer ,measurements and sample times
     */
    @Override
    public void onBarometerValueUpdated(float pressure){
        super.onBarometerValueUpdated(pressure);

        viewModel.updatePressure(new PressureData(System.currentTimeMillis(), pressure)); //update pressure data with new measurements
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /** turn off  listeners when paused */
    @Override
    protected void onPause() {
        //TODO turn the sensors etc off???
        super.onPause();
    }

    /**
     * Handle transactions between fragments in the display
     */
    private void showProperFragment() {
        Fragment fragmentToShow = new StartRecFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView_recording_activity, fragmentToShow);
        transaction.addToBackStack(null);   //don't allow back action as recording needs to be done in the right order
        transaction.commit();
    }

    /**stop recording data to the trajectory native object*/
    public void stopRecording(){
        trajectoryNative = this.endRecording();
    }

    /** disable back button in recording activity as otherwise files will be corrupted */
    @Override
    public void onBackPressed()
    {
        Toast toast = Toast.makeText(this,"Finish the recording before going back!", Toast.LENGTH_SHORT);
    }
}
