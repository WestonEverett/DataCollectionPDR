package com.example.datacollectionpdr.nativedata;

import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class PDRStep {

    public float x;
    public float y;
    public double heading;
    public long initTime;

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
