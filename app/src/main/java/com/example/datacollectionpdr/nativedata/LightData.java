package com.example.datacollectionpdr.nativedata;

/** LightData.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object holding timestamped ambient light data.
 */
public class LightData {

    public float light;
    public long initTime;

    public LightData(long initTime, float light){
        this.initTime = initTime;
        this.light = light;
    }
}
