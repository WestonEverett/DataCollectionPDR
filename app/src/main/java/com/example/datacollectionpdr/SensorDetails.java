package com.example.datacollectionpdr;

public class SensorDetails {

    public String name;
    public String vendor;
    public float res;
    public float power;
    public int version;
    public int type;

    public SensorDetails(String name, String vendor, float res, float power, int version, int type){
        this.name = name;
        this.vendor = vendor;
        this.res = res;
        this.power = power;
        this.version = version;
        this.type = type;
    }
}
