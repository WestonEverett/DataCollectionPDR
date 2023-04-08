package com.example.datacollectionpdr;

import java.util.ArrayList;

//TODO finish commenting
public class SetLengthFloatArray {

    private int maxLength;
    private ArrayList<Float> internalArray;

    public SetLengthFloatArray(int maxLength, Float defaultVal){
        this.maxLength = maxLength;
        internalArray = new ArrayList<>();
        for(int i = 0; i < maxLength; i++){
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

    public float sumArray(){
        float sum = 0f;
        for(int i = 0; i < internalArray.size(); ++i){
            sum+=internalArray.get(i);
        }
        return sum;
    }

    public Float[] getArray(){

        /*
        Float[] floatArray = new Float[internalArray.size()];
        int i = 0;

        for (Float f : internalArray) {
            floatArray[i++] = f;
        }
        */

        return internalArray.toArray(new Float[0]);
    }

}
