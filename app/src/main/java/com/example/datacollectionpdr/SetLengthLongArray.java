package com.example.datacollectionpdr;

import java.util.ArrayList;

public class SetLengthLongArray {

    private int maxLength;
    private ArrayList<Long> internalArray;

    public SetLengthLongArray(int maxLength, long defaultVal){
        this.maxLength = maxLength;
        internalArray = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            internalArray.add(0,defaultVal);
        }
    }

    public SetLengthLongArray(int maxLength){
        this(maxLength, 0);
    }

    public void addValue(long val){
        internalArray.add(0, val);
        while(internalArray.size() > maxLength){
            internalArray.remove(maxLength);
        }
    }

    public long[] getArray(){

        long[] longArray = new long[internalArray.size()];
        int i = 0;

        for (Long l : internalArray) {
            longArray[i++] = (l != null ? l : 0);
        }

        return longArray;
    }

}

