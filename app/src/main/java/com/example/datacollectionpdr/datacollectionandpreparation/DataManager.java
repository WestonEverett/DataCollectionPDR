package com.example.datacollectionpdr.datacollectionandpreparation;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.example.datacollectionpdr.nativedata.APData;
import com.example.datacollectionpdr.nativedata.GNSSData;
import com.example.datacollectionpdr.nativedata.LightData;
import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;
import com.example.datacollectionpdr.nativedata.PositionData;
import com.example.datacollectionpdr.nativedata.PressureData;
import com.example.datacollectionpdr.nativedata.SensorDetails;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.UserPositionData;
import com.example.datacollectionpdr.nativedata.WifiSample;
import com.example.datacollectionpdr.pdrcalculation.AltitudeEstimation;
import com.example.datacollectionpdr.pdrcalculation.GNSSCalculations;
import com.example.datacollectionpdr.pdrcalculation.MadgwickAHRS;
import com.example.datacollectionpdr.pdrcalculation.StepLengthEstimation;

import java.util.ArrayList;
import java.util.HashMap;

/** DataManager.java
 * Authors: Weston Everett, Alexandros Miteloudis Vagionas
 * Affiliation: The University of Edinburgh
 * Description: DataManager handles part of the processing of raw data colleced by DataCollection.
 * When new data is provided by a sensor, DataManager is called to process, package, and send the
 * data to TrajectoryNative and to UI elements. Important functions include:
 * onStepDetectorUpdated() which creates a new PDR step from available data each time it is called,
 * deadWithMotionSample(), which sends a package of accelerometer, gyroscope and rotation values to
 * TrajectoryNative whenever a complete sample package is provided.
 *
 * Datamanager also inherits from permissionsManager, so all permissions are handled through that
 */

public class DataManager extends PermissionsManager implements DataCollection.OnMotionSensorManagerListener{
    private int stepcountDM; //Initial step count when app is opened
    private int curStepcount; //Step count since app was opened
    private MotionSample motionSample; //Collection of acc,gyr, and rot data
    private com.example.datacollectionpdr.datacollectionandpreparation.DataCollection mMotionSensorManager;
    protected TrajectoryNative trajectoryNative; //Class used to send data to server
    private boolean isRecording; //Used to register sensors only when user is recording data
    HashMap<String, WifiObject> WifiData; //Contains a map of all data gathered from WiFi, indexed by MAC address
    private float[] curGravity = new float[]{0f, 9.8f, 0f}; //Holds the latest gravity sensor values
    private float[] curMagnetic; //Holds the latest magnetic field sensor values
    public GNSSData curGNSSData; //Holds the latest location data
    private ArrayList<float[]> accelerations = new ArrayList<>(); //Holds all acceleration values since the last step
    private ArrayList<float[]> gravities = new ArrayList<>(); //Holds all gravity values since the last step
    private MadgwickAHRS madgwickAHRS = new MadgwickAHRS(0.1f, 0); //Algorithm
    private boolean hasStartingAltitude; //Flag for initialising the reference altitude
    AltitudeEstimation altitudeEstimation; //Class that contains variables and functions to help with altitude estimation
    private float lpfPressure = 1013.25f; //Atmospheric pressure at sea level, a good estimate for initial pressure
    private static final float ALPHA = 0.99f; //Very high alpha means strong filtering but slow response time

    /**
     * Initialise data collection tools and flags
     * @param savedInstanceState stores current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMotionSensorManager = new com.example.datacollectionpdr.datacollectionandpreparation.DataCollection(this);
        mMotionSensorManager.setOnMotionSensorManagerListener(this);
        WifiData = new HashMap<>();
        isRecording = false;
        stepcountDM = 0;
        curStepcount = 0;
    }

    /**
     * When user unpauses
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Enable sensors only when recording
        if(isRecording){
            mMotionSensorManager.registerMotionSensors();
        }
    }

    /**
     * When user pauses
     */
    @Override
    protected void onPause(){
        super.onPause();
        //Disable sensors
        mMotionSensorManager.unregisterMotionSensors();
    }

    /**
       Creates PositionData objects
     */
    @Override
    public void onMagnetometerUncalibratedValueUpdated(float[] magneticfield){
        //Log.i("DataM", "MagU data updated");
        PositionData positionData = new PositionData(System.currentTimeMillis(), magneticfield);
        trajectoryNative.addPosition(positionData);
    }

    /**
     Updates current heading estimate with magnetometer values
     */
    @Override
    public void onMagnetometerValueUpdated(float[] magneticfield){
        //Log.i("DataM", "Mag data updated");
        curMagnetic = magneticfield;
        madgwickAHRS.updateMagnetometer(magneticfield);
    }

    /**
     Updates current motionsample with acclerometer values
     */
    @Override
    public void onAccelerometerUncalibratedValueUpdated(float[] acceleration){
        //Log.i("DataM", "AccU data updated");
        motionSample.setAcc(acceleration);
        dealWithMotionSample(motionSample);
    }

    /**
        Stores accelerometer values for step size estimation
     */
    @Override
    public void onAccelerometerValueUpdated(float[] acceleration){
        //Append current accelerometer values to a list; gets reset when a step is detected
        accelerations.add(acceleration);
        //Log.i("DataM", "Acc data updated");
    }

    /**
     Updates current motionsample with gyroscope values
     */
    @Override
    public void onGyroscopeUncalibratedValueUpdated(float[] gyroscope){
        //Log.i("DataM", "GyrU data updated");
        motionSample.setGyro(gyroscope);
        dealWithMotionSample(motionSample);
    }

    /**
     Updates current heading estimate with gyroscope values
     */
    @Override
    public void onGyroscopeValueUpdated(float[] gyroscope){
        //Log.i("DataM", "Gyr data updated");
        madgwickAHRS.updateGyroscope(gyroscope);
    }

    /**
         Makes altitude updates including a low pass filter to get rid of noise
     */
    @Override
    public void onBarometerValueUpdated(float pressure){
        //Log.i("DataM", "Bar data updated");
        //Update TrajectoryNative
        PressureData pressureData = new PressureData(System.currentTimeMillis(), pressure);
        trajectoryNative.addPressure(pressureData);
        //For the first measurement, log a starting altitude as a reference point
        if(!hasStartingAltitude) {
            altitudeEstimation = new AltitudeEstimation(4.0f, 5, AltitudeEstimation.findAltitude(pressure));
            hasStartingAltitude = true;
        }
        //Low-pass filter helps with sudden variations
        lpfPressure = pressure; //ALPHA*lpfPressure + (1-ALPHA)*pressure;
        altitudeEstimation.addAltitude(AltitudeEstimation.findAltitude(lpfPressure));
        Log.i("PressureDelta: ",altitudeEstimation.altitudeDelta()+"; FloorsChanged: " + altitudeEstimation.floorsChanged());
    }

    /**
         Creates LightData objects for trajectoryNative
     */
    @Override
    public void onAmbientLightValueChanged(float luminance){
        //Log.i("DataM", "AmbL data updated");
        LightData lightData = new LightData(System.currentTimeMillis(),luminance);
        trajectoryNative.addLight(lightData);
    }

    /**
    Stub implementation for proximity measurements
     */
    @Override
    public void onProximityValueUpdated(float proximity){
        //Log.i("DataM", "Prox data updated");
    }

    /**
         Stores gravity estimation for heading estimation
     */
    @Override
    public void onGravityValueUpdated(float[] gravity){
        //Log.i("DataM", "Grav data updated");
        curGravity = gravity;
        gravities.add(curGravity);
        madgwickAHRS.updateAccelerometer(gravity);
    }

    /**
     Updates current motionsample with rotation vector values
     */
    @Override
    public void onRotationVectorValueUpdated(float[] rotationvector){
        //Log.i("DataM", "RotV data updated");
        motionSample.setRotVector(rotationvector);
        dealWithMotionSample(motionSample);
    }

    /**
         Stores complete wifi sample
     */
    @Override
    public void onWifiValueUpdated(HashMap<String, WifiObject> map){
       Log.i("DataM", "WiFi data updated");
       WifiSample wifiSample = new WifiSample(System.currentTimeMillis());
       wifiSample.addMacSampleDict(map);
       trajectoryNative.addWifi(wifiSample);
       for(String mac : map.keySet()){
           WifiData.put(mac, map.get(mac));
       }

    }

    /**
         Creates GNSS location estimation object
     */
    @Override
    public void onLocationValueUpdated(String provider, float acc, float alt, long initTime, float lon, float lat, float speed){
        Log.i("DataM", "GNSS data updated");
        //Log.i(provider, lon +" "+ lat);
        GNSSData gnssData = new GNSSData(provider,acc,alt,initTime,lon,lat,speed);
        curGNSSData = gnssData;
        trajectoryNative.addGNSS(gnssData);
    }

    /**
         Estimates new PDRStep based on stored sensor data/heading/stepsize
     */
    @Override
    public void onStepDetectorUpdated(){
        Log.i("DataM", "StpD data updated");
        //Estimate step length

        float stepSize = StepLengthEstimation.findStepLength(accelerations, gravities);

        //Update PDR with estimated step length, heading, and floor level and add it to TrajectoryNative
        PDRStep pdrStep = new PDRStep(stepSize, madgwickAHRS.findHeading(), System.currentTimeMillis());
        pdrStep.setEstFloor(altitudeEstimation.floorsChanged());
        trajectoryNative.addPDRStep(pdrStep);
        this.newPDRStep(pdrStep);
        //Reset arrays for step length estimation
        accelerations = new ArrayList<>();
        gravities = new ArrayList<>();
    }

    /**
      Stores current change in stepcount (since program start)
     */
    @Override
    public void onStepCountValueUpdated(int stepcount){
        //Log.i("DataM", "StpC data updated");
        //motionSample.steps = stepcount-step countDM; //Step delta between new and old data
        if(stepcountDM == 0){
            stepcountDM = stepcount;
        }

        curStepcount = stepcount;
    }

    /**
         Checks if all necessary values for motionsample availale, builds new one if so
     */
    private void dealWithMotionSample(MotionSample motionSample){
        // Check if all flags are set
        if(motionSample.isComplete()){
            // Create new motionsample
            newCompleteMotionSample(motionSample);
            //Log.i("Motion sample", String.valueOf(System.currentTimeMillis()));
            motionSample.steps = curStepcount - stepcountDM;
            motionSample.initTime = System.currentTimeMillis();
            trajectoryNative.addMotion(motionSample);
            this.newCompleteMotionSample(motionSample);
            this.motionSample = new MotionSample();
        }
    }

    /**
     * Sends device sensor information (e.g. manufacturer, model name, etc.) to trajectoryNative
     */
    @Override
    public void onSensorInfoCollected(SensorDetails accInfo, SensorDetails gyrInfo,
                                      SensorDetails magInfo, SensorDetails barInfo,
                                      SensorDetails ambInfo, SensorDetails rotInfo){
        trajectoryNative.setAccInfo(accInfo);
        trajectoryNative.setGyroInfo(gyrInfo);
        trajectoryNative.setMagInfo(magInfo);
        trajectoryNative.setBaroInfo(barInfo);
        trajectoryNative.setLightInfo(ambInfo);
        trajectoryNative.setRotVectorInfo(rotInfo);
    }

    /**
     * Overwritten to trigger behavior in UI
     * @param motionSample includes all parameters relating to motion
     */
    protected void newCompleteMotionSample(MotionSample motionSample){
    }

    /**
     * Overwritten to trigger behavior in UI
     * @param pdrStep includes step size and heading
     */
    protected void newPDRStep(PDRStep pdrStep){
    }

    /**
     * Ends recording
     * @return TrajectoryNative object containing collected data
     */
    public TrajectoryNative endRecording(){
        mMotionSensorManager.unregisterMotionSensors();
        isRecording = false;
        for(WifiObject wifiObject : WifiData.values()){
            APData apdata = new APData(wifiObject);
            trajectoryNative.addAPData(apdata);
        }
        return trajectoryNative;
    }

    /**
     * Begins recording
     * @param initPos Initial User-inputted location/heading
     */
    public void startRecording(UserPositionData initPos){
        motionSample = new MotionSample();
        madgwickAHRS = new MadgwickAHRS(0.1f, initPos.heading);
        trajectoryNative = new TrajectoryNative(System.currentTimeMillis(), initPos);
        trajectoryNative.setDataID("no_id_entered");
        stepcountDM = curStepcount;
        isRecording = true;
        mMotionSensorManager.registerMotionSensors();


    }
}
