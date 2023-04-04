package com.example.datacollectionpdr.pdrcalculation;

import android.hardware.SensorManager;
import android.util.Log;

public class AltitudeEstimation {
    private static float FLOOR_HEIGHT = 4f; // In metres
    private float altitude = 0.0f;
    private float startingAltitude = 0.0f;

    public void setAltitude(float altitude){
        this.altitude = altitude;
    }
    public void setStartingAltitude(float startingAltitude){
        this.startingAltitude = startingAltitude;
    }
    public float getAltitude(){
        return this.altitude;
    }
    public float getStartingAltitude(){
        return this.startingAltitude;
    }
    public float altitudeDelta(){return this.altitude - this.startingAltitude;}
    public int floorsChanged(){
        int numOfFloorsChanged = Math.round(this.altitudeDelta()/FLOOR_HEIGHT);
        return numOfFloorsChanged;
    }
    public float findAltitude(float pressure){
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,pressure);
    }

}
