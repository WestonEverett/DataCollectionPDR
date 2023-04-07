package com.example.datacollectionpdr.pdrcalculation;

import android.hardware.SensorManager;
import android.util.Log;
/** AltitudeEstimation.java
 * Author: Alexandros Miteloudis Vagionas
 * Affiliation: The University of Edinburgh
 * Description: Class for holding and processing barometer data into altitude change and an estimate
 * of the user's current floor. Floor height is assumed to be 4 metres, but this can vary. Over a 30
 * minute period, the barometer drift only results in an error of +-1 metre, so no drift correction
 * is necessary as a full floor change is larger.
 */
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
    //Returns altitude change since the first barometer measurement
    public float altitudeDelta(){return this.altitude - this.startingAltitude;}
    //Returns an estimate of the number of floors changed as an integer
    public int floorsChanged(){
        int numOfFloorsChanged = Math.round(this.altitudeDelta()/FLOOR_HEIGHT);
        return numOfFloorsChanged;
    }
    //All altitudes are compared to a standard atmospheric pressure of 1013.25 millibar
    public float findAltitude(float pressure){
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,pressure);
    }

}
