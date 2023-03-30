package com.example.datacollectionpdr.nativedata;

import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class PDRStep {

    public float x;
    public float y;
    public double heading;
    public long initTime;

    public static class DotProduct {
        public static float dotProd(float[] x, float[] y) {
            if (x.length != y.length)
                throw new RuntimeException("Arrays must be same size");
            float sum = 0;
            for (int i = 0; i < x.length; i++)
                sum += x[i] * y[i];/*from  w ww.  j  a  va2 s  .c  o m*/
            Log.e("DotProdSum", String.valueOf(sum));
            return sum;
        }

        public static float[] zAxisAcceleration(ArrayList<float[]> accelerations, float[] gravity){
            float[] processedArray = new float[accelerations.size()];
            float[] normalizedGravity = normalize(gravity);
            for(int i = 0; i< accelerations.size(); i++){
                float[] filteredAcc = accelerations.get(i);
                filteredAcc[0] = filteredAcc[0] - gravity[0];
                filteredAcc[1] = filteredAcc[1] - gravity[1];
                filteredAcc[2] = filteredAcc[2] - gravity[2];
                processedArray[i] = dotProd(filteredAcc,normalizedGravity);
            }
            return processedArray;
        }

        public static float[] normalize(float[] vector) {
            float magnitude = 0.0f;
            for (float component : vector) {
                magnitude += component * component;
            }
            magnitude = (float) Math.sqrt(magnitude);

            if (magnitude == 0.0) {
                throw new ArithmeticException("Cannot normalize a zero-length vector.");
            }

            float[] normalized = new float[vector.length];
            for (int i = 0; i < vector.length; i++) {
                normalized[i] = vector[i] / magnitude;
            }
            return normalized;
        }
    }

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

    public PDRStep(ArrayList<float[]> accelerations, float[] gravity, float[] magneticFieldValues, long initTime){
        final float CONSTANT_K = 7/48f; //From Analog Devices paper "Using the ADXL202 in Pedometer and Personal Navigation Applications"
        float stepLengthEstimate = 1;
        this.heading = calculateOrientation(gravity, magneticFieldValues);
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
