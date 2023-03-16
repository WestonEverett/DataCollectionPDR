package com.example.datacollectionpdr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DataCollection implements SensorEventListener {

    private OnMotionSensorManagerListener motionSensorManagerListener;

    private SensorManager sensorManager;
    private Sensor Accelerometer;
    private Sensor Gyroscope;
    private Sensor mMagneticField;
    private Sensor Barometer;
    private Sensor Gravity;

    public DataCollection(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        mMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        Gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        Barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        Gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    public void setOnMotionSensorManagerListener(OnMotionSensorManagerListener motionSensorManagerListener){
        this.motionSensorManagerListener = motionSensorManagerListener;
    }

    public void unregisterMotionSensors(){
        sensorManager.unregisterListener(this);
    }

    public void registerMotionSensors(){
        sensorManager.registerListener(this, mMagneticField, 10000);
        sensorManager.registerListener(this, Accelerometer, 10000);
        sensorManager.registerListener(this, Gyroscope, 10000);
        sensorManager.registerListener(this, Gravity, 10000);
        sensorManager.registerListener(this, Barometer, 1000000);
    }

    private double h;
    private float gravity[] = new float[3];

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                motionSensorManagerListener.onAccValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                motionSensorManagerListener.onGyroValueUpdated(sensorEvent.values);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                h = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] +
                        sensorEvent.values[2] * sensorEvent.values[2]);

                motionSensorManagerListener.onMagValueUpdated(sensorEvent.values, (float) h);
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

    }
}



