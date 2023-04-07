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

/** DataCollection.java
 * Authors: Alexandros Miteloudis Vagionas, Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: DataCollection handles the collection and part of the packaging of sensor data.
 * It initialises, pauses, and resumes all sensors. The data it collects is sent to DataManager.
 */
public class DataCollection implements SensorEventListener {
    private static final int WIFI_UPDATE_INTERVAL = 300; //300ms update interval for WiFi
    private static final int UPDATES_BEFORE_WIFI_PURGE = 5; //5 data aggregations before list purge

    //Function returns packaged sensor information in a SensorDetails object
    public SensorDetails sensorDetails(int type){
        String name = sensorManager.getDefaultSensor(type).getName();
        String vendor = sensorManager.getDefaultSensor(type).getVendor();
        float res = sensorManager.getDefaultSensor(type).getResolution();
        float power = sensorManager.getDefaultSensor(type).getPower();
        int version = sensorManager.getDefaultSensor(type).getVersion();
        return new SensorDetails(name, vendor,res,power,version,type);
    };
    //Initialising objects, sensors, and variables
    //Managers and listeners
    WifiManager wifiManager;
    LocationManager locationManager;
    LocationListener locationListener;
    private OnMotionSensorManagerListener motionSensorManagerListener;

    private SensorManager sensorManager;
    //Sensors
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

    //Sensor information
    SensorDetails accInfo;
    SensorDetails gyrInfo;
    SensorDetails magInfo;
    SensorDetails barInfo;
    SensorDetails ambInfo;
    SensorDetails rotInfo;
    private Context context;

    private List<ScanResult> wifiScanList = null; // Initialise WiFi scan list

    // WiFi data works differently to all other sensors
    // Stored as hashmap of BSSID and maximum observed signal level in dBm
    HashMap<String, WifiObject> wifiData = new HashMap<>();
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context c, Intent intent) {


        }
    };

    //DataCollection LocationListener gets provider, accuracy, altitude, time, longitude, latitude and speed.
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
                //Log.i("Longitude", String.valueOf(lon));
                //Log.i("Latitude", String.valueOf(lat));
                motionSensorManagerListener.onLocationValueUpdated(provider,acc,alt,initTime,lon,lat,speed);
            }
            else Log.e("Location","=null");
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
            Toast.makeText(context, "Please enable location services.", Toast.LENGTH_SHORT).show();
        }
        //Enable WiFi if disabled
        if(wifiManager.getWifiState()==wifiManager.WIFI_STATE_DISABLED){
            Toast.makeText(context, "WiFi disabled, enabling...", Toast.LENGTH_SHORT).show();
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
        sensorManager.registerListener(this, StepDetector,10000); // 100 Samples/s
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

    //Timestamps for WiFi data aggregation
    private long lastTimestamp = System.currentTimeMillis();
    //Counter for number of currently aggregated samples
    private int purgeWifiDataCount;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        //Scan for WiFi networks every interval and increment count
        long currentTimestamp = System.currentTimeMillis();
        if(currentTimestamp-lastTimestamp > WIFI_UPDATE_INTERVAL){
            //Check that permissions have been given before asking for the WiFi and location scan results
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

                wifiScanList = wifiManager.getScanResults(); //Get WiFi scan results
                //Log.i("Number of WiFi networks:", String.valueOf(wifiScanList.size())); //Log the number of wifi networks detected
                //For all networks in the scan
                for(int i = 0; i<wifiScanList.size(); i++){
                    // Temporary ID and signal level variables
                    int power = wifiScanList.get(i).level;
                    String id = wifiScanList.get(i).BSSID;
                    String ssid = wifiScanList.get(i).SSID;
                    long freq = wifiScanList.get(i).frequency;

                    WifiObject curWifiData = new WifiObject(power, id, ssid, freq);
                    //Log.i(id, String.valueOf(power));
                    // If the entry doesn't exist, add it to the hashmap.
                    if(!wifiData.containsKey(id)){
                        wifiData.put(id, curWifiData);
                    }
                    // Else update entry with the maximum power value
                    else if(wifiData.get(id).power < power){
                        wifiData.put(id,curWifiData);
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                wifiManager.startScan();
            }
            lastTimestamp = currentTimestamp;
            purgeWifiDataCount++;
            //Log.i("Timestamp", String.valueOf(currentTimestamp));
        }
        //When count reaches max number of aggregated samples, send data to DataManager and clear data
        if(purgeWifiDataCount == UPDATES_BEFORE_WIFI_PURGE){
            motionSensorManagerListener.onWifiValueUpdated(wifiData); // Once we have an aggregate of wifi samples, send it to DataManager
            Log.i("Wifi", "Wifis in scan: " + wifiData.size());
            wifiData = new HashMap<>(); // Clear wifi data
            purgeWifiDataCount = 0;
        }

        //Call DataManager whenever a sensor updates
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                motionSensorManagerListener.onMagnetometerUncalibratedValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                motionSensorManagerListener.onAccelerometerUncalibratedValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                motionSensorManagerListener.onGyroscopeUncalibratedValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                motionSensorManagerListener.onMagnetometerValueUpdated(sensorEvent.values);
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

    //Functions initialised here and instantiated in DataManager
    public interface OnMotionSensorManagerListener{
        void onMagnetometerUncalibratedValueUpdated(float[] magneticfield);
        void onAccelerometerUncalibratedValueUpdated(float[] acceleration);
        void onGyroscopeUncalibratedValueUpdated(float[] gyroscope);
        void onMagnetometerValueUpdated(float[] magneticfield);
        void onAccelerometerValueUpdated(float[] acceleration);
        void onGyroscopeValueUpdated(float[] gyroscope);
        void onBarometerValueUpdated(float pressure);
        void onAmbientLightValueChanged(float luminance);
        void onProximityValueUpdated(float proximity);
        void onGravityValueUpdated(float[] gravity);
        void onRotationVectorValueUpdated(float[] rotationvector);
        void onStepDetectorUpdated();
        void onStepCountValueUpdated(int stepcount);
        void onWifiValueUpdated(HashMap<String, WifiObject> map);
        void onLocationValueUpdated(String provider, float acc, float alt, long initTime, float lon, float lat, float speed);
        void onSensorInfoCollected(SensorDetails AccInfo, SensorDetails GyrInfo,
                                   SensorDetails MagInfo, SensorDetails BarInfo,
                                   SensorDetails AmbInfo, SensorDetails RotInfo);
    }

    //Code left for potential future implementations of dealing with accuracy changes
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



