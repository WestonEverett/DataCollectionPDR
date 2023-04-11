package com.example.datacollectionpdr.nativedata;
import com.example.datacollectionpdr.data.Sensor_Info;

/** SensorDetails.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object for holding sensor information, e.g. manufacturer, version, etc.
 */

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

    public SensorDetails(Sensor_Info sensor_info){
        this.name = sensor_info.getName();
        this.vendor = sensor_info.getVendor();
        this.res = sensor_info.getResolution();
        this.power = sensor_info.getPower();
        this.version = sensor_info.getVersion();
        this.type = sensor_info.getType();
    }
}
