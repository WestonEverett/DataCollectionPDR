package com.example.datacollectionpdr.nativedata;

public class GNSSData {

    public String provider;
    public float acc;
    public float alt;
    public long initTime;
    public float lon;
    public float lat;
    public float speed;

    public GNSSData(String provider, float acc, float alt, long initTime, float lon, float lat, float speed){
        this.provider = provider;
        this.acc = acc;
        this.alt = alt;
        this.initTime = initTime;
        this.lon = lon;
        this.lat = lat;
        this.speed = speed;
    }
}
