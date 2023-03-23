package com.example.datacollectionpdr.datacollectionandpreparation;
package com.example.datacollectionpdr.nativedata;
import android.os.Bundle;
import android.util.Log;

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
        //Log.i("DataM", "MagU data updated");
        PositionData positionData = new PositionData(System.currentTimeMillis(), magneticfield);
    }
    @Override
    public void onMagnetometerValueUpdated(float[] magneticfield, float h){
        //Log.i("DataM", "Mag data updated");
    }
    @Override
    public void onAccelerometerUncalibratedValueUpdated(float[] acceleration){
        //Log.i("DataM", "Mag data updated");
        motionSample.setAcc(acceleration);
        dealWithMotionSample(motionSample);
    }
    @Override
    public void onAccelerometerValueUpdated(float[] acceleration){
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
        //Log.i("DataM", "Gyr data updated");
    }
    @Override
    public void onBarometerValueUpdated(float pressure){
        //Log.i("DataM", "Bar data updated");
        PressureData pressureData = new PressureData(System.currentTimeMillis(), pressure);
    }
    @Override
    public void onAmbientLightValueChanged(float luminance){
        //Log.i("DataM", "AmbL data updated");
        LightData lightData = new LightData(System.currentTimeMillis(),luminance);
    }
    @Override
    public void onProximityValueUpdated(float proximity){
        //Log.i("DataM", "Prox data updated");
    }
    @Override
    public void onGravityValueUpdated(float[] gravity){
        //Log.i("DataM", "Grav data updated");
    }
    @Override
    public void onRotationVectorValueUpdated(float[] rotationvector){
        //Log.i("DataM", "RotV data updated");
        motionSample.setRotVector(rotationvector);
        dealWithMotionSample(motionSample);
    }
    @Override
    public void onWifiValueUpdated(HashMap map){
       Log.i("DataM", "WiFi data updated");
       WifiSample wifiSample = new WifiSample(System.currentTimeMillis());
       wifiSample.addMacSampleDict(map);
    }
    @Override
    public void onStepDetectorUpdated(){
        //Log.i("DataM", "StpD data updated");
    }
    @Override
    public void onStepCountValueUpdated(int stepcount){
        //Log.i("DataM", "StpC data updated");
        stepcountDM = stepcount;
    }
    private void dealWithMotionSample(MotionSample motionSample){
        // Check if all flags are set
        if(motionSample.isComplete()){
            // Create new motionsample
            //Log.i("Motion sample", String.valueOf(System.currentTimeMillis()));
            this.motionSample = new MotionSample(System.currentTimeMillis(),stepcountDM);
        }
    }
}
