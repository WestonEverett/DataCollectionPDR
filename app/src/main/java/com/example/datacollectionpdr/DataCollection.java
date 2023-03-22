package com.example.datacollectionpdr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Objects;

public class DataCollection implements SensorEventListener {

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

    public DataCollection(Context context){
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
    }

    // Function returns sensor vendor or version
    public String sensorDetails(String query, int type){
        String result = "";
        if(Objects.equals(query, "Vendor")) result = sensorManager.getDefaultSensor(type).getVendor();
        else if(Objects.equals(query, "Version")) result = String.valueOf(sensorManager.getDefaultSensor(type).getVersion());
        return result;
    };

    public void setOnMotionSensorManagerListener(OnMotionSensorManagerListener motionSensorManagerListener){
        this.motionSensorManagerListener = motionSensorManagerListener;
    }

    public void unregisterMotionSensors(){
        sensorManager.unregisterListener(this);
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
    }

    private double h;
    final float alpha = .8f;
    private float gravity[] = new float[3];

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                h = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] +
                        sensorEvent.values[2] * sensorEvent.values[2]);

                motionSensorManagerListener.onMagValueUpdated(sensorEvent.values, (float) h);
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                motionSensorManagerListener.onAccValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                motionSensorManagerListener.onGyroValueUpdated(sensorEvent.values);
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

    public interface OnMotionSensorManagerListener{
        void onAccValueUpdated(float[] acceleration);
        void onGyroValueUpdated(float[] gyroscope);
        void onMagValueUpdated(float[] magneticfield, float h);
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



