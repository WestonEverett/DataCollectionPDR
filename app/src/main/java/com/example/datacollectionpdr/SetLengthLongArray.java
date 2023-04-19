package com.example.datacollectionpdr;

import java.util.ArrayList;

//TODO finish commenting
/**
 * Description:
 * Author:  Weston Everett
 * Affiliation: The University of Edinburgh
 */
public class SetLengthLongArray {

    private int maxLength;
    private ArrayList<Long> internalArray;

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

    public void addValue(Long val){
        internalArray.add(0, val);
        while(internalArray.size() > maxLength){
            internalArray.remove(maxLength);
        }
    }

    public Long[] getArray(){

        return internalArray.toArray(new Long[0]);
    }

}

