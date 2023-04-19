package com.example.datacollectionpdr.nativedata;

/** GNSSData.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object holding location data.
 */
public class GNSSData {

    public String provider;
    public float acc;
    public float alt; //altitude
    public long initTime; //initialization time
    public float lon; //longitude
    public float lat; //latitude
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
