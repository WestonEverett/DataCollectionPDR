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

    /**
     * COnstructor for AP Data object
     * @param mac mac address
     * @param ssid name, like "Eduroam"
     * @param freq frequency, like 5GHz
     */
    public APData(long mac, String ssid, long freq){
        this.mac = mac;
        this.ssid = ssid;
        this.freq = freq;
    }

    /**
     * Sets object values
     * @param macStr mac address as string
     * @param ssid ssid as string
     * @param freq frequency
     */
    public APData(String macStr, String ssid, long freq){
        this.mac = Long.parseLong(macStr.replace(":", ""), 16);
        this.ssid = ssid;
        this.freq = freq;
    }

    /**
     * Sets object values
     * @param wifiObject includes several values relating to WiFi
     */
    public APData(WifiObject wifiObject){
        this.mac = Long.parseLong(wifiObject.mac.replace(":", ""), 16);
        this.ssid = wifiObject.ssid;
        this.freq = wifiObject.freq;
    }
}
