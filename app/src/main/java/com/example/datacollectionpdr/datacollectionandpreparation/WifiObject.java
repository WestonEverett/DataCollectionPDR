package com.example.datacollectionpdr.datacollectionandpreparation;

/** WifiObject.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object for holding WiFi data.
 */
public class WifiObject {

    public int power;
    public String mac;
    public String ssid;
    public long freq;

    public WifiObject(int power, String mac, String ssid, long freq) {
        this.power = power;
        this.mac = mac;
        this.ssid = ssid;
        this.freq = freq;
    }
}
