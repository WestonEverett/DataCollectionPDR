package com.example.datacollectionpdr;

import java.util.ArrayList;

public class SetLengthFloatArray {

    private int maxLength;
    private ArrayList<Float> internalArray;

    public SetLengthFloatArray(int maxLength, float defaultVal){
        this.maxLength = maxLength;
        internalArray = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            internalArray.add(0,defaultVal);
        }
    }

    public SetLengthFloatArray(int maxLength){
        this(maxLength, 0f);
    }

    public void addValue(float val){
        internalArray.add(0, val);
        while(internalArray.size() > maxLength){
            internalArray.remove(maxLength);
        }
    }

    public float[] getArray(){

        float[] floatArray = new float[internalArray.size()];
        int i = 0;

        for (Float f : internalArray) {
            floatArray[i++] = (f != null ? f : 0f);
        }

        return floatArray;
    }

}
