package com.example.datacollectionpdr.nativedata;

/** PressureData.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object holding timestamped barometer data.
 */
public class PressureData {

    public float pressure;
    public long timestamp;

    public PressureData(long timestamp, float pressure){
        this.pressure = pressure;
        this.timestamp = timestamp;
    }
}
