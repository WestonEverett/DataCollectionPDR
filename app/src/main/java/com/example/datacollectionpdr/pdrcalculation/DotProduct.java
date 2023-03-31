package com.example.datacollectionpdr.pdrcalculation;

import android.util.Log;

import java.util.ArrayList;

public class DotProduct {
    public static float dotProd(float[] x, float[] y) {
        if (x.length != y.length)
            throw new RuntimeException("Arrays must be same size");
        float sum = 0;
        for (int i = 0; i < x.length; i++)
            sum += x[i] * y[i];/*from  w ww.  j  a  va2 s  .c  o m*/
        //Log.e("DotProdSum", String.valueOf(sum));
        return sum;
    }

    public static float[] zAxisAcceleration(ArrayList<float[]> accelerations, ArrayList<float[]> gravities){
        int minArraySize = Math.min(accelerations.size(), gravities.size());
        float[] processedArray = new float[minArraySize];

        for(int i = 0; i< minArraySize; i++){
            float[] filteredAcc = accelerations.get(i);
            float[] normalizedGravity = normalize(gravities.get(i));
            filteredAcc[0] = filteredAcc[0] - gravities.get(i)[0];
            filteredAcc[1] = filteredAcc[1] - gravities.get(i)[1];
            filteredAcc[2] = filteredAcc[2] - gravities.get(i)[2];
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
