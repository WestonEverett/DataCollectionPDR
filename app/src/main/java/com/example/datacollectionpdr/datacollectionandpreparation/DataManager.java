package com.example.datacollectionpdr.datacollectionandpreparation;

import android.os.Bundle;

import java.util.HashMap;

public class DataManager extends PermissionsManager implements DataCollection.OnMotionSensorManagerListener{

    private DataCollection mMotionSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMotionSensorManager = new DataCollection(this);
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

    }

    @Override
    public void onMagnetometerValueUpdated(float[] magneticfield, float h){

    }

    @Override
    public void onAccelerometerUncalibratedValueUpdated(float[] acceleration){

    }

    @Override
    public void onAccelerometerValueUpdated(float[] acceleration){

    }

    @Override
    public void onGyroscopeUncalibratedValueUpdated(float[] gyroscope){

    }

    @Override
    public void onGyroscopeValueUpdated(float[] gyroscope){

    }

    @Override
    public void onBarometerValueUpdated(float pressure){

    }
    @Override
    public void onAmbientLightValueChanged(float luminance){

    }
    @Override
    public void onProximityValueUpdated(float proximity){

    }
    @Override
    public void onGravityValueUpdated(float[] gravity){

    }
    @Override
    public void onWifiValueUpdated(String[] wifis, HashMap map){

    }
    @Override
    public void onStepDetectorUpdated(){

    }
    @Override
    public void onStepCountValueUpdated(int stepcount){

    }
}
