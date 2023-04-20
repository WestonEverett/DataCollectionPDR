package com.example.datacollectionpdr;

import java.util.ArrayList;

/**
 * Description:
 * Author:  Weston Everett
 * Affiliation: The University of Edinburgh
 * Essentially a buffer containing the last maxLength values added to it
 */
public class SetLengthLongArray {

    private int maxLength;
    private ArrayList<Long> internalArray;

    /**
     * Initialization with the number of values to hold at once and the default vvalue to fill with
     * @param maxLength number of values to hold at once
     * @param defaultVal value to fill unfilled slots with
     */
    public SetLengthLongArray(int maxLength, Long defaultVal){
        this.maxLength = maxLength;
        internalArray = new ArrayList<>();
        for(int i = 0; i < maxLength; i++){
            internalArray.add(0,defaultVal);
        }
    }

    public SetLengthLongArray(int maxLength){
        this(maxLength, 0L);
    }

    /**
     * Adds new value and cuts the last value if necessary
     * @param val
     */
    public void addValue(Long val){
        internalArray.add(0, val);
        while(internalArray.size() > maxLength){
            internalArray.remove(maxLength);
        }
    }

    /**
     * @return Currently stored array
     */
    public Long[] getArray(){

        return internalArray.toArray(new Long[0]);
    }

}

