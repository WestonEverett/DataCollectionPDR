package com.example.datacollectionpdr.nativedata;

import com.example.datacollectionpdr.datacollectionandpreparation.WifiObject;

/** APData.java
 * Authors: Weston Everett, Alexandros Miteloudis Vagionas
 * Affiliation: The University of Edinburgh
 * Description: Contains information about a WiFi reading
 */
public class APData {

    public long mac;
    public String ssid;
    public long freq;

    public APData(long mac, String ssid, long freq){
        this.mac = mac;
        this.ssid = ssid;
        this.freq = freq;
    }

    public APData(String macStr, String ssid, long freq){
        this.mac = Long.parseLong(macStr.replace(":", ""), 16);
        this.ssid = ssid;
        this.freq = freq;
    }

    public APData(WifiObject wifiObject){
        this.mac = Long.parseLong(wifiObject.mac.replace(":", ""), 16);
        this.ssid = wifiObject.ssid;
        this.freq = wifiObject.freq;
    }
}
