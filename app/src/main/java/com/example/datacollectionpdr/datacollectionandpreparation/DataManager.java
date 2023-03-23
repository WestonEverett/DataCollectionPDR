package com.example.datacollectionpdr.datacollectionandpreparation;
import android.os.Bundle;
import android.util.Log;

import com.example.datacollectionpdr.nativedata.LightData;
import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;
import com.example.datacollectionpdr.nativedata.PositionData;
import com.example.datacollectionpdr.nativedata.PressureData;
import com.example.datacollectionpdr.nativedata.SensorDetails;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.WifiSample;

import java.util.HashMap;

public class DataManager extends PermissionsManager implements DataCollection.OnMotionSensorManagerListener{
    private int stepcountDM;
    private MotionSample motionSample;
    private com.example.datacollectionpdr.datacollectionandpreparation.DataCollection mMotionSensorManager;
    private TrajectoryNative trajectoryNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        motionSample = new MotionSample(System.currentTimeMillis(),stepcountDM);
        mMotionSensorManager = new com.example.datacollectionpdr.datacollectionandpreparation.DataCollection(this);
        mMotionSensorManager.setOnMotionSensorManagerListener(this);
        trajectoryNative = new TrajectoryNative(System.currentTimeMillis());
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
        trajectoryNative.addPosition(positionData);
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
        trajectoryNative.addPressure(pressureData);
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
       trajectoryNative.addWifi(wifiSample);
    }
    @Override
    public void onStepDetectorUpdated(){
        //Log.i("DataM", "StpD data updated");
        //PDRStep pdrStep = new PDRStep(1.f,2.f, System.currentTimeMillis());
    }
    @Override
    public void onStepCountValueUpdated(int stepcount){
        //Log.i("DataM", "StpC data updated");
        motionSample.steps = stepcount-stepcountDM; //Step delta between new and old data
        stepcountDM = stepcount;
    }
    private void dealWithMotionSample(MotionSample motionSample){
        // Check if all flags are set
        if(motionSample.isComplete()){
            // Create new motionsample
            //Log.i("Motion sample", String.valueOf(System.currentTimeMillis()));
            trajectoryNative.addMotion(motionSample);
            this.motionSample = new MotionSample(System.currentTimeMillis(),stepcountDM);
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

}
