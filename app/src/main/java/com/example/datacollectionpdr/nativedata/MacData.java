package com.example.datacollectionpdr.nativedata;

/** MacData.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Class for holding and processing MAC address data in the way required by
 * TrajectoryNative.
 */
public class MacData {

    public long mac;
    public int rssi;

    public MacData(long mac, int rssi){
        this.mac = mac;
        this.rssi = rssi;
    }

    public MacData(String macStr, int rssi){
        this.rssi = rssi;
        this.mac = Long.parseLong(macStr.replace(":",""), 16);
    }
}
