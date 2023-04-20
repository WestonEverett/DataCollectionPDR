package com.example.datacollectionpdr;

import java.util.ArrayList;

//TODO finish commenting
/**
 * Description:
 * Author:  Weston Everett
 * Affiliation: The University of Edinburgh
 * Set length buffer for holding floats
 */
public class SetLengthFloatArray {

    private int maxLength;
    private ArrayList<Float> internalArray;

    /**
     * Cosntructor for new instance
     * @param maxLength number of values to store
     * @param defaultVal default value
     */
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

    /**
     * Add new float to array, cutting last value if necessary
     * @param val
     */
    public void addValue(float val){
        internalArray.add(0, val);
        while(internalArray.size() > maxLength){
            internalArray.remove(maxLength);
        }
    }

    /**
     * @return returns current array
     */
    public Float[] getArray(){

        return internalArray.toArray(new Float[0]);
    }

}
