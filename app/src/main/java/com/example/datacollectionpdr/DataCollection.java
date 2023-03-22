package com.example.datacollectionpdr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DataCollection implements SensorEventListener {

    private static final int WIFI_UPDATE_INTERVAL = 5000; // 5s update interval for WiFi
    private static final int UPDATES_BEFORE_WIFI_PURGE = 5; // 5 data aggregations before list purge
    WifiManager wifiManager;
    String wifis[];
    private OnMotionSensorManagerListener motionSensorManagerListener;

    private SensorManager sensorManager;
    private Sensor Accelerometer;
    private Sensor AccelerometerUncalibrated;
    private Sensor Gyroscope;
    private Sensor GyroscopeUncalibrated;
    private Sensor MagneticField;
    private Sensor MagneticFieldUncalibrated;
    private Sensor Barometer;
    private Sensor AmbientLight;
    private Sensor Proximity;
    private Sensor Gravity;
    private Sensor StepDetector;
    private Sensor StepCounter;
    private Context context;

    HashMap<String, Integer> map = new HashMap<>();
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            wifis = new String[wifiScanList.size()];
            Log.e("WiFi", String.valueOf(wifiScanList.size()));
            for(int i = 0; i<wifiScanList.size(); i++){
                int power = wifiScanList.get(i).level;
                String id = wifiScanList.get(i).BSSID;
                // If the entry doesn't exist, add it to the list.
                if(!map.containsKey(id)){
                    map.put(id, power);
                }
                // Else update it with the maximum power value
                else{
                    int chosenIntensity = (map.get(id) > power) ? map.get(id): power;
                    map.put(id,chosenIntensity);
                    map.put(id,wifiScanList.get(i).level);
                }

                wifis[i] = wifiScanList.get(i).BSSID +
                        "    " + String.valueOf(wifiScanList.get(i).level);
                Log.e("WiFi", String.valueOf(wifis[i]));
            }
            motionSensorManagerListener.onWifiValueUpdated(wifis, map);
        }
    };

    public DataCollection(Context context){
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        //Initialize sensors
        //Uncalibrated sensors for data submission
        MagneticFieldUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        AccelerometerUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        GyroscopeUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        //Calibrated sensors for data processing
        MagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        AmbientLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        StepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        StepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.getWifiState()==wifiManager.WIFI_STATE_DISABLED){
            wifiManager.setWifiEnabled(true);
        }



        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        //Toast.makeText(this, "Scanning WiFi..", Toast.LENGTH_SHORT).show();
    }

    // Function returns sensor vendor or version
    public String sensorDetails(String query, int type){
        String result = "";
        if(Objects.equals(query, "Vendor")) result = sensorManager.getDefaultSensor(type).getVendor();
        else if(Objects.equals(query, "Version")) result = String.valueOf(sensorManager.getDefaultSensor(type).getVersion());
        else if(Objects.equals(query, "Power")) result = String.valueOf(sensorManager.getDefaultSensor(type).getPower());
        return result;
    };

    public void setOnMotionSensorManagerListener(OnMotionSensorManagerListener motionSensorManagerListener){
        this.motionSensorManagerListener = motionSensorManagerListener;
    }

    public void unregisterMotionSensors(){
        sensorManager.unregisterListener(this);
        context.unregisterReceiver(wifiScanReceiver);
    }

    public void registerMotionSensors(){
        sensorManager.registerListener(this, MagneticFieldUncalibrated, 10000); // 100 Samples/s
        sensorManager.registerListener(this, AccelerometerUncalibrated, 10000); // 100 Samples/s
        sensorManager.registerListener(this, GyroscopeUncalibrated, 10000); // 100 Samples/s
        sensorManager.registerListener(this, MagneticField, 10000); // 100 Samples/s
        sensorManager.registerListener(this, Accelerometer, 10000); // 100 Samples/s
        sensorManager.registerListener(this, Gyroscope, 10000); // 100 Samples/s
        sensorManager.registerListener(this, Barometer, 1000000); // 1 Sample/s
        sensorManager.registerListener(this, AmbientLight, 1000000); // 1 Sample/s
        sensorManager.registerListener(this, Proximity, 1000000); // 1 Sample/s
        sensorManager.registerListener(this, Gravity, 10000); // 100 Samples/s
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private double h;
    final float alpha = .8f;
    private float gravity[] = new float[3];
    private long lastTimestamp = System.currentTimeMillis();
    private long currentTimestamp;
    private int purgeWifiDataCount;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        currentTimestamp = System.currentTimeMillis();
        if(currentTimestamp-lastTimestamp > WIFI_UPDATE_INTERVAL){
            wifiManager.startScan();
            lastTimestamp = currentTimestamp;
            purgeWifiDataCount++;
            Log.e("Timestamp", String.valueOf(currentTimestamp));
        }
        if(purgeWifiDataCount == UPDATES_BEFORE_WIFI_PURGE){
            map.clear();
            purgeWifiDataCount = 0;
            List<String> keys = new ArrayList<>(map.keySet());
            Log.i("Map cleared", String.valueOf(keys));
        }
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                h = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] +
                        sensorEvent.values[2] * sensorEvent.values[2]);

                motionSensorManagerListener.onMagnetometerUncalibratedValueUpdated(sensorEvent.values, (float) h);
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                motionSensorManagerListener.onAccelerometerUncalibratedValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                motionSensorManagerListener.onGyroscopeUncalibratedValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                h = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] +
                        sensorEvent.values[2] * sensorEvent.values[2]);

                motionSensorManagerListener.onMagnetometerValueUpdated(sensorEvent.values, (float) h);
                break;

            case Sensor.TYPE_ACCELEROMETER:
                motionSensorManagerListener.onAccelerometerValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_GYROSCOPE:
                motionSensorManagerListener.onGyroscopeValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_PRESSURE:
                motionSensorManagerListener.onBarometerValueUpdated(sensorEvent.values[0]);
                break;

            case Sensor.TYPE_LIGHT:
                motionSensorManagerListener.onAmbientLightValueChanged(sensorEvent.values[0]);
                break;

            case Sensor.TYPE_PROXIMITY:
                motionSensorManagerListener.onProximityValueUpdated(sensorEvent.values[0]);
                break;

            case Sensor.TYPE_GRAVITY:
                motionSensorManagerListener.onGravityValueUpdated(sensorEvent.values);
                break;

        }
    }

    public interface OnMotionSensorManagerListener{
        void onMagnetometerUncalibratedValueUpdated(float[] magneticfield, float h);
        void onAccelerometerUncalibratedValueUpdated(float[] acceleration);
        void onGyroscopeUncalibratedValueUpdated(float[] gyroscope);
        void onMagnetometerValueUpdated(float[] magneticfield, float h);
        void onAccelerometerValueUpdated(float[] acceleration);
        void onGyroscopeValueUpdated(float[] gyroscope);
        void onBarometerValueUpdated(float pressure);
        void onAmbientLightValueChanged(float luminance);
        void onProximityValueUpdated(float proximity);
        void onGravityValueUpdated(float[] gravity);
        void onWifiValueUpdated(String[] wifis, HashMap map);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:

                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:

                break;

            case Sensor.TYPE_ACCELEROMETER:
                //Do stuff
                break;

            case Sensor.TYPE_GYROSCOPE:
                //Do other stuff
                break;

            case Sensor.TYPE_PRESSURE:

                break;

            case Sensor.TYPE_LIGHT:

                break;

            case Sensor.TYPE_PROXIMITY:

                break;

            case Sensor.TYPE_GRAVITY:

                break;

        }
    }
}



