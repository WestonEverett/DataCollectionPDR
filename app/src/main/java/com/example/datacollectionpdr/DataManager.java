package com.example.datacollectionpdr;

import android.os.Bundle;

import java.util.HashMap;

public class DataManager extends PermissionsManager implements com.example.datacollectionpdr.DataCollection.OnMotionSensorManagerListener{
    private int stepcountDM;
    private MotionSample motionSample;
    private com.example.datacollectionpdr.DataCollection mMotionSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        motionSample = new MotionSample(System.currentTimeMillis(),stepcountDM);
        mMotionSensorManager = new com.example.datacollectionpdr.DataCollection(this);
        mMotionSensorManager.setOnMotionSensorManagerListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMotionSensorManager.registerMotionSensors();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mMotionSensorManager.unregisterMotionSensors();
    }

    @Override
    public void onMagnetometerUncalibratedValueUpdated(float[] magneticfield, float h){
        PositionData positionData = new PositionData(System.currentTimeMillis(), magneticfield);
    }

    @Override
    public void onMagnetometerValueUpdated(float[] magneticfield, float h){

    }

    @Override
    public void onAccelerometerUncalibratedValueUpdated(float[] acceleration){
        motionSample.setAcc(acceleration);
        dealWithMotionSample(motionSample);
    }

    @Override
    public void onAccelerometerValueUpdated(float[] acceleration){

    }

    @Override
    public void onGyroscopeUncalibratedValueUpdated(float[] gyroscope){
        motionSample.setGyro(gyroscope);
        dealWithMotionSample(motionSample);
    }

    @Override
    public void onGyroscopeValueUpdated(float[] gyroscope){

    }

    @Override
    public void onBarometerValueUpdated(float pressure){
        PressureData pressureData = new PressureData(System.currentTimeMillis(), pressure);
    }
    @Override
    public void onAmbientLightValueChanged(float luminance){
        LightData lightData = new LightData(System.currentTimeMillis(),luminance);
    }
    @Override
    public void onProximityValueUpdated(float proximity){

    }
    @Override
    public void onGravityValueUpdated(float[] gravity){

    }
    @Override
    public void onWifiValueUpdated(HashMap map){
       WifiSample wifiSample = new WifiSample(System.currentTimeMillis());
       wifiSample.addMacSampleDict(map);
    }
    @Override
    public void onStepDetectorUpdated(){

    }
    @Override
    public void onStepCountValueUpdated(int stepcount){
        stepcountDM = stepcount;
    }

    private MotionSample dealWithMotionSample(MotionSample motionSample){
        // Check if all flags are set
        if(motionSample.isComplete()){
            // Create new motionsample
            motionSample = new MotionSample(System.currentTimeMillis(),stepcountDM);
        }
        return motionSample;
    }
}
