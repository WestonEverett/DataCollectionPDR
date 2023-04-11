package com.example.datacollectionpdr.nativedata;

/** PositionData.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object for holding timestamped magnetometer data
 */
public class PositionData {

    public long initTime;
    public float[] mag;

    public PositionData(long initTime, float[] mag){
        this.initTime = initTime;
        this.mag = mag;
    }
}
