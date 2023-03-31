package com.example.datacollectionpdr.nativedata;

import android.hardware.SensorManager;
import android.util.Log;

import com.example.datacollectionpdr.pdrcalculation.DotProduct;
import com.example.datacollectionpdr.pdrcalculation.MadgwickAHRS;

import java.util.ArrayList;
import java.util.Arrays;

public class PDRStep {

    public float x;
    public float y;
    public double heading;
    public long initTime;
    public float estRelativeAltitude;

    public PDRStep(float x, float y, long initTime){
        this.x = x;
        this.y = y;
        this.initTime = initTime;
    }

    public PDRStep(float[] gravity, float[] magneticFieldValues, long initTime){

        this.heading = calculateOrientation(gravity, magneticFieldValues);
        float stepLengthEstimate = 1;
        this.x = stepLengthEstimate * (float) Math.sin(heading);
        this.y = stepLengthEstimate * (float) Math.cos(heading);
        Log.i("PDRSTEP", "Heading" + this.heading);
        this.initTime = initTime;
    }

    public PDRStep(ArrayList<float[]> accelerations, float heading, float[] gravity, float[] magneticFieldValues, long initTime){

        ////// Finding heading //////
        //this.heading = calculateOrientation(gravity, magneticFieldValues);
        this.heading = heading;

        ////// Finding step length //////
        final float CONSTANT_K = 7/48f; //From Analog Devices paper "Using the ADXL202 in Pedometer and Personal Navigation Applications"
        float stepLengthEstimate = 1;

        //Finds dot product of each acc vector with respect to gravity
        float[] zAxisAcceleration = DotProduct.zAxisAcceleration(accelerations,gravity);
        //Sorts array for easy max and min values
        Arrays.sort(zAxisAcceleration);
        Log.i("AccLength", String.valueOf(accelerations.size()));
        // (Max acc value - min acc value)^(-4)
        if(zAxisAcceleration.length != 0) {
            stepLengthEstimate = (float) Math.sqrt(Math.sqrt(zAxisAcceleration[zAxisAcceleration.length - 1] - zAxisAcceleration[0])) * CONSTANT_K;
        }
        Log.i("StepLength", String.valueOf(stepLengthEstimate));

        ////// Finding x and y lengths //////
        this.x = stepLengthEstimate * (float) Math.sin(heading);
        this.y = stepLengthEstimate * (float) Math.cos(heading);
        Log.i("PDRSTEP", "Heading" + this.heading);
        this.initTime = initTime;
    }

    private double calculateOrientation(float[] gravity, float[] magneticFieldValues) {
        /*
        Calculates the current orientation of the phone (relative to the world)
         */

        //variables for storing output of calculations
        float[] values = new float[3];
        float[] R = new float[9];

        //calculates rotation matrix based on gravity and the current magnetic field
        SensorManager.getRotationMatrix(R,
                null, gravity, magneticFieldValues);

        SensorManager.getOrientation(R, values);

        return values[0];
    }
}
