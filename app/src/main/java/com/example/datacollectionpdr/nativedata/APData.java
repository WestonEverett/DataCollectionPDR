package com.example.datacollectionpdr.nativedata;

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
}
