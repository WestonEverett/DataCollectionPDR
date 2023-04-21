package com.example.datacollectionpdr.pdrcalculation;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/** StepLengthEstimation.java
 * Author: Alexandros Miteloudis Vagionas, Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Class for requesting and holding permission information from the user. PathFinder
 * requires WiFi, Internet, Location, and Activity permissions for the WiFi, location, and step
 * detection.
 */

public class StepLengthEstimation {
    private final static float CONSTANT_K = 7/48f; //From Analog Devices paper "Using the ADXL202 in Pedometer and Personal Navigation Applications"
    private final static float defaultStepLengthEstimate = 1;

    /**
     * Finds step length
     * @param accelerations accelerometer
     * @param gravities gravity values
     * @return
     */
    public static float findStepLength(ArrayList<float[]> accelerations, ArrayList<float[]> gravities){
        //Finds dot product of each acc vector with respect to gravity
        float[] zAxisAcceleration = DotProduct.zAxisAcceleration(accelerations, gravities);
        //Sorts array for easy max and min values
        Arrays.sort(zAxisAcceleration);
        Log.i("AccLength", String.valueOf(accelerations.size()));
        Log.i("GraLength", String.valueOf(gravities.size()));
        // (Max acc value - min acc value)^(-4)
        if (zAxisAcceleration.length != 0) {
            return (float) Math.sqrt(Math.sqrt(zAxisAcceleration[zAxisAcceleration.length - 1] - zAxisAcceleration[0])) * CONSTANT_K;
        }
        Log.i("StepLength", "zAxis is 0 length");
        return defaultStepLengthEstimate;
    }
}
