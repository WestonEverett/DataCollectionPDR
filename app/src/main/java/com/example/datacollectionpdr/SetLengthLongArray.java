package com.example.datacollectionpdr;

import java.util.ArrayList;

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

        /*
        Long[] longArray = new Long[internalArray.size()];
        int i = 0;

        for (Long l : internalArray) {
            longArray[i++] = (l);
        }
        */

        return internalArray.toArray(new Long[0]);
    }

}

