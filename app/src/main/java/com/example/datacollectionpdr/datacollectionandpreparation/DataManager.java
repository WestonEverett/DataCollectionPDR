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
import com.example.datacollectionpdr.nativedata.WifiSample;
import com.example.datacollectionpdr.pdrcalculation.AltitudeEstimation;
import com.example.datacollectionpdr.pdrcalculation.MadgwickAHRS;

import java.util.ArrayList;
import java.util.HashMap;

public class DataManager extends PermissionsManager implements DataCollection.OnMotionSensorManagerListener{
    private int stepcountDM;
    private int curStepcount;
    private MotionSample motionSample;
    private MotionSample lastMotionSample = new MotionSample();
    private com.example.datacollectionpdr.datacollectionandpreparation.DataCollection mMotionSensorManager;
    private TrajectoryNative trajectoryNative;
    private boolean isRecording;
    HashMap<String, WifiObject> WifiData;

    private float[] curGravity = new float[]{0f, 9.8f, 0f};
    private float[] curMagnetic;
    private ArrayList<float[]> accelerations = new ArrayList<>();

    private MadgwickAHRS madgwickAHRS = new MadgwickAHRS(0.1f);
    private float startingAltitude;
    private boolean hasStartingAltitude;
    AltitudeEstimation altitudeEstimation = new AltitudeEstimation();

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
        }
        //Altitude change from the first barometer measurement
        float currentRelativeAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure) - startingAltitude;
        altitudeEstimation.setAltitude(currentRelativeAltitude);
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
        trajectoryNative.addGNSS(gnssData);
    }
    @Override
    public void onStepDetectorUpdated(){
        Log.i("DataM", "StpD data updated");
        PDRStep pdrStep = new PDRStep(accelerations, madgwickAHRS.findHeading(), curGravity, curMagnetic, System.currentTimeMillis());
        accelerations = new ArrayList<>();
        trajectoryNative.addPDRStep(pdrStep);
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
            lastMotionSample = this.motionSample;
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

    public void startRecording(){
        motionSample = new MotionSample();
        trajectoryNative = new TrajectoryNative(System.currentTimeMillis());
        trajectoryNative.setDataID("hmmmmm");
        stepcountDM = curStepcount;
        isRecording = true;
        mMotionSensorManager.registerMotionSensors();
    }
}
