package com.example.datacollectionpdr.pdrcalculation;

import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.example.datacollectionpdr.SetLengthFloatArray;

/** AltitudeEstimation.java
 * Author: Alexandros Miteloudis Vagionas, Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Class for holding and processing barometer data into altitude change and an estimate
 * of the user's current floor. Floor height is assumed to be 4 metres, but this can vary. Over a 30
 * minute period, the barometer drift only results in an error of +-1 metre, so no drift correction
 * is necessary as a full floor change is larger.
 */
public class AltitudeEstimation {
    private final float floor_height; // In metres
    private final float varianceThreshold = .1f;
    private float curFloorAltitude;
    private float lastAltitude;
    private SetLengthFloatArray recentAltitudes;
    private boolean changingFloors;
    private int currentFloor;

    /**
     * Altitude estimator constructor, responsivle for estimating floors
     * @param floorConstant assumed normal height of a floor (between 2-6 meters)
     * @param memoryLength number of values to buffer (trade-off of accuracy for response time)
     * @param startingAltitude initial floor altitude
     */
    public AltitudeEstimation(float floorConstant, int memoryLength, float startingAltitude){
        this.floor_height = floorConstant;
        this.recentAltitudes = new SetLengthFloatArray(memoryLength, startingAltitude);
        this.curFloorAltitude = startingAltitude;
        this.lastAltitude = startingAltitude;
        this.changingFloors = false;
        this.currentFloor = 0;
    }

    /**
     * adds an altitude measurement to the buffer storage
     * @param altitude measurement to be added
     */
    public void addAltitude(float altitude){
        recentAltitudes.addValue(altitude);
        this.lastAltitude = altitude;

        this.checkFloors();
    }

    /**
     * Calculates variance of recent altitude measurements
     * High variance means the phone is likely in the process of changing floors
     * When the phone stabilizes, it is assumed to be stable on a new floor
     */
    private void checkFloors(){
        float variance = recentAltitudes.getVariance();
        boolean currentlyChanging = (variance > varianceThreshold);

        Log.i("checkFloorsVar", "Variance: " + variance);

        if(this.changingFloors && !currentlyChanging){
            this.curFloorAltitude = this.recentAltitudes.getMean();
            this.currentFloor = this.currentFloor + Math.round(curFloorAltitude/floor_height);
        } else if(!currentlyChanging){
            this.curFloorAltitude = this.recentAltitudes.getMean();
        }


        changingFloors = currentlyChanging;
    }

    /**
     * Returns altitude change since the first barometer measurement
     * @return
     */
    public float altitudeDelta(){return this.lastAltitude - this.curFloorAltitude;}

    /**
     * returns the current floor (relative to start point)
     * @return
     */
    public int floorsChanged(){
        return currentFloor;
    }

    /**
     * Static function for calculating altitude
     * @param pressure pressure to get altitude of
     * @return
     */
    public static float findAltitude(float pressure){
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,pressure);
    }

}
