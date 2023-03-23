package com.example.datacollectionpdr;

public class PressureData {

    public float pressure;
    public long timestamp;

    public PressureData(long timestamp, float pressure){
        this.pressure = pressure;
        this.timestamp = timestamp;
    }
}
