package com.example.datacollectionpdr.nativedata;

public class PressureData {

    public float pressure;
    public long timestamp;

    public PressureData(long timestamp, float pressure){
        this.pressure = pressure;
        this.timestamp = timestamp;
    }
}
