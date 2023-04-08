package com.example.datacollectionpdr.pdrcalculation;

import android.util.Log;

import java.util.ArrayList;

/** PermissionsManager.java
 * Author: Alexandros Miteloudis Vagionas
 * Affiliation: The University of Edinburgh
 * Description: Used for processing data relating to step size estimates
 */

public class DotProduct {
    public static float dotProd(float[] x, float[] y) {
        if (x.length != y.length)
            throw new RuntimeException("Arrays must be same size");
        float sum = 0;
        for (int i = 0; i < x.length; i++)
            sum += x[i] * y[i];
        //Log.e("DotProdSum", String.valueOf(sum));
        return sum;
    }

    public static float[] zAxisAcceleration(ArrayList<float[]> accelerations, ArrayList<float[]> gravities){
        int minArraySize = Math.min(accelerations.size(), gravities.size());
        float[] processedArray = new float[minArraySize];

        for(int i = 0; i< minArraySize; i++){
            float[] filteredAcc = accelerations.get(i);
            //Normalise gravity with respect to the absolute Z-axis (up-down)
            float[] normalizedGravity = normalize(gravities.get(i));
            //Remove the gravity values from the acceleration
            filteredAcc[0] = filteredAcc[0] - gravities.get(i)[0];
            filteredAcc[1] = filteredAcc[1] - gravities.get(i)[1];
            filteredAcc[2] = filteredAcc[2] - gravities.get(i)[2];
            //Dot product provides the acceleration in the absolute Z-axis, and thus the 'bounce'
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
