package com.example.datacollectionpdr.datacollectionandpreparation;

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
