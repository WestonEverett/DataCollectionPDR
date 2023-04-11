package com.example.datacollectionpdr.pdrcalculation;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/** StepLengthEstimation.java
 * Author: Alexandros Miteloudis Vagionas
 * Affiliation: The University of Edinburgh
 * Description: Class for requesting and holding permission information from the user. PathFinder
 * requires WiFi, Internet, Location, and Activity permissions for the WiFi, location, and step
 * detection.
 */

public class StepLengthEstimation {
    ////// Finding step length //////
    private final float CONSTANT_K = 7/48f; //From Analog Devices paper "Using the ADXL202 in Pedometer and Personal Navigation Applications"
    private float stepLengthEstimate = 1;

    private ArrayList<float[]> accelerations;
    private ArrayList<float[]> gravities;

    public void setAccelerations(ArrayList<float[]> accelerations){
        this.accelerations = accelerations;
    }

    public ArrayList<float[]> getAccelerations(){
        return this.accelerations;
    }
    public void setGravities(ArrayList<float[]> gravities){
        this.gravities = gravities;
    }

    public ArrayList<float[]> getGravity(){
        return this.gravities;
    }

    public float findStepLength(){
        //Finds dot product of each acc vector with respect to gravity
        float[] zAxisAcceleration = DotProduct.zAxisAcceleration(this.accelerations, this.gravities);
        //Sorts array for easy max and min values
        Arrays.sort(zAxisAcceleration);
        Log.i("AccLength", String.valueOf(this.accelerations.size()));
        Log.i("GraLength", String.valueOf(this.gravities.size()));
        // (Max acc value - min acc value)^(-4)
        if (zAxisAcceleration.length != 0) {
            stepLengthEstimate = (float) Math.sqrt(Math.sqrt(zAxisAcceleration[zAxisAcceleration.length - 1] - zAxisAcceleration[0])) * this.CONSTANT_K;
        }
        Log.i("StepLength", String.valueOf(stepLengthEstimate));
        return stepLengthEstimate;
    }
}
