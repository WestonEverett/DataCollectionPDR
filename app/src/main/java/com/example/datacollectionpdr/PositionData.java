package com.example.datacollectionpdr;

public class PositionData {

    public long initTime;
    public float[] mag;

    public PositionData(long initTime, float[] mag){
        this.initTime = initTime;
        this.mag = mag;
    }
}
