package com.example.datacollectionpdr.datacollectionandpreparation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.datacollectionpdr.nativedata.SensorDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataCollection implements SensorEventListener {

    private static final int WIFI_UPDATE_INTERVAL = 5000; // 5s update interval for WiFi
    private static final int UPDATES_BEFORE_WIFI_PURGE = 5; // 5 data aggregations before list purge

    // Function returns sensor info object
    public SensorDetails sensorDetails(int type){
        String name = sensorManager.getDefaultSensor(type).getName();
        String vendor = sensorManager.getDefaultSensor(type).getVendor();
        float res = sensorManager.getDefaultSensor(type).getResolution();
        float power = sensorManager.getDefaultSensor(type).getPower();
        int version = sensorManager.getDefaultSensor(type).getVersion();
        return new SensorDetails(name, vendor,res,power,version,type);
    };
    WifiManager wifiManager;
    LocationManager locationManager;
    LocationListener locationListener;
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
    private Sensor RotationVector;
    private Sensor StepDetector;
    private Sensor StepCounter;
    SensorDetails accInfo;
    SensorDetails gyrInfo;
    SensorDetails magInfo;
    SensorDetails barInfo;
    SensorDetails ambInfo;
    SensorDetails rotInfo;
    private Context context;

    // WiFi data works differently to all other sensors
    // Stored as hashmap of BSSID and maximum observed signal level in dBm
    HashMap<String, Integer> WifiData = new HashMap<>();
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = null; // Initialise WiFi scan list
            //Check that permissions have been given before asking for the WiFi scan results
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                wifiScanList = wifiManager.getScanResults(); //Get WiFi scan results
                Log.i("Number of WiFi networks:", String.valueOf(wifiScanList.size())); //Log the number of wifi networks detected
                //For all networks in the scan
                for(int i = 0; i<wifiScanList.size(); i++){
                    // Temporary ID and signal level variables
                    int power = wifiScanList.get(i).level;
                    String id = wifiScanList.get(i).BSSID;
                    // If the entry doesn't exist, add it to the hashmap.
                    if(!WifiData.containsKey(id)){
                        WifiData.put(id, power);
                    }
                    // Else update entry with the maximum power value
                    else{
                        int chosenIntensity = (WifiData.get(id) > power) ? WifiData.get(id): power;
                        WifiData.put(id,chosenIntensity);
                        WifiData.put(id,wifiScanList.get(i).level);
                    }
                }
                //motionSensorManagerListener.onWifiValueUpdated(WifiData);
            }
        }
    };

    class  dcLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(@NonNull Location location){
            if(location != null){
                String provider = location.getProvider();
                float acc = location.getAccuracy();
                float alt = (float) location.getAltitude();
                long initTime = location.getTime();
                float lon = (float) location.getLongitude();
                float lat = (float) location.getLatitude();
                float speed = location.getSpeed();
                Log.i("Location",provider);
                motionSensorManagerListener.onLocationValueUpdated(provider,acc,alt,initTime,lon,lat,speed);
            }
        }
    }

    public DataCollection(Context context){
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        //Initialize sensors
        //Uncalibrated sensors for data submission
        MagneticFieldUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        AccelerometerUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        GyroscopeUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        //Calibrated and virtual sensors for data processing
        MagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        AmbientLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        RotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        StepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        StepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        //Initialise location manager and listener, prompt the user to enable GPS if disabled
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new dcLocationListener();
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(context, "Open GPS", Toast.LENGTH_SHORT).show();
        }
        //Enable WiFi if disabled
        if(wifiManager.getWifiState()==wifiManager.WIFI_STATE_DISABLED){
            wifiManager.setWifiEnabled(true);
        }
    }

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
        sensorManager.registerListener(this, RotationVector, 10000); // 100 Samples/s
        sensorManager.registerListener(this,StepDetector,10000); // 100 Samples/s
        sensorManager.registerListener(this, StepCounter, 10000); // 100 Samples/s
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();

        //Check if sensor exists before trying to get its details
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accInfo = sensorDetails(Sensor.TYPE_ACCELEROMETER);
            Log.i("HasSensor", "Acc");
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            gyrInfo = sensorDetails(Sensor.TYPE_GYROSCOPE);
            Log.i("HasSensor", "Gyr");
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            magInfo = sensorDetails(Sensor.TYPE_MAGNETIC_FIELD);
            Log.i("HasSensor", "Mag");
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            barInfo = sensorDetails(Sensor.TYPE_PRESSURE);
            Log.i("HasSensor", "Bar");
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            ambInfo = sensorDetails(Sensor.TYPE_LIGHT);
            Log.i("HasSensor", "Amb");
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null){
            rotInfo = sensorDetails(Sensor.TYPE_ROTATION_VECTOR);
            Log.i("HasSensor", "Rot");
        }
        motionSensorManagerListener.onSensorInfoCollected(accInfo, gyrInfo, magInfo, barInfo, ambInfo, rotInfo);
    }

    //Magnetic field stuff, remove?
    private double h;
    final float alpha = .8f;
    private float gravity[] = new float[3];
    //Timestamps for WiFi data aggregation
    private long lastTimestamp = System.currentTimeMillis();
    private long currentTimestamp;
    //Counter for number of currently aggregated samples
    private int purgeWifiDataCount;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        //Scan for WiFi networks every interval and increment count
        currentTimestamp = System.currentTimeMillis();
        if(currentTimestamp-lastTimestamp > WIFI_UPDATE_INTERVAL){
            wifiManager.startScan();
            lastTimestamp = currentTimestamp;
            purgeWifiDataCount++;
            Log.i("Timestamp", String.valueOf(currentTimestamp));
        }
        //When count reaches max number of aggregated samples, send data to DataManager and clear data
        if(purgeWifiDataCount == UPDATES_BEFORE_WIFI_PURGE){
            motionSensorManagerListener.onWifiValueUpdated(WifiData); // Once we have an aggregate of wifi samples, send it to DataManager
            WifiData = new HashMap<>(); // Clear wifi data
            purgeWifiDataCount = 0;
            List<String> keys = new ArrayList<>(WifiData.keySet());
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

            case Sensor.TYPE_ROTATION_VECTOR:
                motionSensorManagerListener.onRotationVectorValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_STEP_DETECTOR:
                motionSensorManagerListener.onStepDetectorUpdated();
                break;

            case Sensor.TYPE_STEP_COUNTER:
                motionSensorManagerListener.onStepCountValueUpdated((int)sensorEvent.values[0]);
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
        void onRotationVectorValueUpdated(float[] rotationvector);
        void onStepDetectorUpdated();
        void onStepCountValueUpdated(int stepcount);
        void onWifiValueUpdated(HashMap map);
        void onLocationValueUpdated(String provider, float acc, float alt, long initTime, float lon, float lat, float speed);
        void onSensorInfoCollected(SensorDetails AccInfo, SensorDetails GyrInfo,
                                   SensorDetails MagInfo, SensorDetails BarInfo,
                                   SensorDetails AmbInfo, SensorDetails RotInfo);
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
            case Sensor.TYPE_ROTATION_VECTOR:

                break;
            case Sensor.TYPE_STEP_DETECTOR:

                break;
            case Sensor.TYPE_STEP_COUNTER:

                break;

        }
    }
}



