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

public class DataManager extends PermissionsManager implements DataCollection.OnMotionSensorManagerListener{
    private int stepcountDM;
    private int curStepcount;
    private MotionSample motionSample;
    private com.example.datacollectionpdr.datacollectionandpreparation.DataCollection mMotionSensorManager;
    protected TrajectoryNative trajectoryNative;
    private boolean isRecording;
    HashMap<String, WifiObject> WifiData;

    private float[] curGravity = new float[]{0f, 9.8f, 0f};
    private float[] curMagnetic;
    public GNSSData curGNSSData;
    private ArrayList<float[]> accelerations = new ArrayList<>();
    private ArrayList<float[]> gravities = new ArrayList<>();


    private MadgwickAHRS madgwickAHRS = new MadgwickAHRS(0.1f, 0);
    private float startingAltitude;
    private boolean hasStartingAltitude;
    AltitudeEstimation altitudeEstimation = new AltitudeEstimation();
    private float lpfPressure;
    private float hpfPressure;
    private static final float ALPHA = 0.8f;

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
    @Override
    protected void onResume() {
        super.onResume();
        if(isRecording){
            mMotionSensorManager.registerMotionSensors();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        mMotionSensorManager.unregisterMotionSensors();
    }
    @Override
    public void onMagnetometerUncalibratedValueUpdated(float[] magneticfield, float h){
        //Log.i("DataM", "MagU data updated");
        PositionData positionData = new PositionData(System.currentTimeMillis(), magneticfield);
        trajectoryNative.addPosition(positionData);
    }
    @Override
    public void onMagnetometerValueUpdated(float[] magneticfield, float h){
        //Log.i("DataM", "Mag data updated");
        curMagnetic = magneticfield;
        madgwickAHRS.updateMagnetometer(magneticfield);
    }
    @Override
    public void onAccelerometerUncalibratedValueUpdated(float[] acceleration){
        //Log.i("DataM", "AccU data updated");
        motionSample.setAcc(acceleration);
        dealWithMotionSample(motionSample);
    }
    @Override
    public void onAccelerometerValueUpdated(float[] acceleration){
        accelerations.add(acceleration);
        //float[] linearAcc = new float[]{acceleration[0] - curGravity[0], acceleration[1] - curGravity[1], acceleration[2] - curGravity[2]};
        //madgwickAHRS.updateAccelerometer(linearAcc);
        //Log.i("DataM", "Acc data updated");
    }
    @Override
    public void onGyroscopeUncalibratedValueUpdated(float[] gyroscope){
        //Log.i("DataM", "GyrU data updated");
        motionSample.setGyro(gyroscope);
        dealWithMotionSample(motionSample);
    }
    @Override
    public void onGyroscopeValueUpdated(float[] gyroscope){
        madgwickAHRS.updateGyroscope(gyroscope);
        //Log.i("DataM", "Gyr data updated");
    }
    @Override
    public void onBarometerValueUpdated(float pressure){
        //Log.i("DataM", "Bar data updated");
        PressureData pressureData = new PressureData(System.currentTimeMillis(), pressure);
        trajectoryNative.addPressure(pressureData);
        if(!hasStartingAltitude) {
            altitudeEstimation.setStartingAltitude(SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure));
            hasStartingAltitude = true;
            //Altitude change from the first barometer measurement
            float currentRelativeAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure) - startingAltitude;
            altitudeEstimation.setAltitude(currentRelativeAltitude);
            altitudeEstimation.floorsChanged();
        }
        lpfPressure = ALPHA*lpfPressure + (1-ALPHA)*pressure;
        hpfPressure = pressure - lpfPressure;
        altitudeEstimation.changeAltitude(hpfPressure);
    }
    @Override
    public void onAmbientLightValueChanged(float luminance){
        //Log.i("DataM", "AmbL data updated");
        LightData lightData = new LightData(System.currentTimeMillis(),luminance);
        trajectoryNative.addLight(lightData);
    }
    @Override
    public void onProximityValueUpdated(float proximity){
        //Log.i("DataM", "Prox data updated");
    }
    @Override
    public void onGravityValueUpdated(float[] gravity){
        //Log.i("DataM", "Grav data updated");
        curGravity = gravity;
        gravities.add(curGravity);
        madgwickAHRS.updateAccelerometer(gravity);
    }
    @Override
    public void onRotationVectorValueUpdated(float[] rotationvector){
        //Log.i("DataM", "RotV data updated");
        motionSample.setRotVector(rotationvector);
        dealWithMotionSample(motionSample);
    }
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
    @Override
    public void onLocationValueUpdated(String provider, float acc, float alt, long initTime, float lon, float lat, float speed){
        Log.i("DataM", "GNSS data updated");
        //Log.i(provider, lon +" "+ lat);
        GNSSData gnssData = new GNSSData(provider,acc,alt,initTime,lon,lat,speed);
        curGNSSData = gnssData;
        trajectoryNative.addGNSS(gnssData);
    }
    @Override
    public void onStepDetectorUpdated(){
        Log.i("DataM", "StpD data updated");
        //Estimate step length
        StepLengthEstimation stepLengthEstimate = new StepLengthEstimation();
        stepLengthEstimate.setAccelerations(accelerations);
        stepLengthEstimate.setGravities(gravities);
        float stepSize = stepLengthEstimate.findStepLength();

        //Update PDR with estimated step length, heading, and floor level and add it to TrajectoryNative
        PDRStep pdrStep = new PDRStep(stepSize, madgwickAHRS.findHeading(), System.currentTimeMillis());
        pdrStep.setEstFloor(altitudeEstimation.floorsChanged());
        trajectoryNative.addPDRStep(pdrStep);
        this.newPDRStep(pdrStep);
        //Reset arrays for step length estimation
        accelerations = new ArrayList<>();
        gravities = new ArrayList<>();
    }
    @Override
    public void onStepCountValueUpdated(int stepcount){
        //Log.i("DataM", "StpC data updated");
        //motionSample.steps = stepcount-step countDM; //Step delta between new and old data
        if(stepcountDM == 0){
            stepcountDM = stepcount;
        }

        curStepcount = stepcount;
    }
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

    protected void newCompleteMotionSample(MotionSample motionSample){
        //overwritten to trigger behavior in UI
    }

    protected void newPDRStep(PDRStep pdrStep){
        //overwritten to trigger behavior in UI
    }

    public TrajectoryNative endRecording(){
        mMotionSensorManager.unregisterMotionSensors();
        isRecording = false;
        for(WifiObject wifiObject : WifiData.values()){
            APData apdata = new APData(wifiObject);
            trajectoryNative.addAPData(apdata);
        }
        return trajectoryNative;
    }

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
