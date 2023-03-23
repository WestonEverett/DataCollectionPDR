package com.example.datacollectionpdr;

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
