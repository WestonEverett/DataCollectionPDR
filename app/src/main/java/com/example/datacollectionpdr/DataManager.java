package com.example.datacollectionpdr;

import android.os.Bundle;

public class DataManager extends PermissionsManager implements com.example.datacollectionpdr.DataCollection.OnMotionSensorManagerListener{

    private com.example.datacollectionpdr.DataCollection mMotionSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onAccValueUpdated(float[] acceleration) {

    }

    @Override
    public void onGyroValueUpdated(float[] gyroscope) {

    }

    @Override
    public void onMagValueUpdated(float[] magneticfield, float h) {

    }
}
