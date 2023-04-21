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

    public AltitudeEstimation(float floorConstant, int memoryLength, float startingAltitude){
        this.floor_height = floorConstant;
        this.recentAltitudes = new SetLengthFloatArray(memoryLength, startingAltitude);
        this.curFloorAltitude = startingAltitude;
        this.lastAltitude = startingAltitude;
        this.changingFloors = false;
        this.currentFloor = 0;
    }

    public void addAltitude(float altitude){
        recentAltitudes.addValue(altitude);
        this.lastAltitude = altitude;

        this.checkFloors();
    }

    private void checkFloors(){
        float variance = recentAltitudes.getVariance();
        boolean currentlyChanging = (variance > varianceThreshold);

        Log.i("checkFloorsVar", "Variance: " + variance);

        if(this.changingFloors && !currentlyChanging){
            this.curFloorAltitude = this.recentAltitudes.getMean();
            this.currentFloor = this.currentFloor + Math.round(curFloorAltitude/floor_height);
        }

        changingFloors = currentlyChanging;
    }

    /**
     * Returns altitude change since the first barometer measurement
     * @return
     */
    public float altitudeDelta(){return this.lastAltitude - this.curFloorAltitude;}
    //Returns an estimate of the number of floors changed as an integer
    public int floorsChanged(){
        return currentFloor;
    }
    //All altitudes are compared to a standard atmospheric pressure of 1013.25 millibar
    public static float findAltitude(float pressure){
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,pressure);
    }

}
