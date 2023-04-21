package com.example.datacollectionpdr.nativedata;

import com.example.datacollectionpdr.datacollectionandpreparation.WifiObject;

import java.util.ArrayList;
import java.util.HashMap;

/** WifiSample.java
 * Authors: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Class for holding detected WiFi network MAC addresses (BSSID)
 */
public class WifiSample {

    public long initTime;
    public ArrayList<MacData> macSamples;

    /**
     * Constructor for wifi sample
     * @param initTime time sample was taken
     */
    public WifiSample(long initTime){
        this.initTime = initTime;
        this.macSamples = new ArrayList<>();
    }

    /**
     * Adds a MAC sample to the list
     * @param macData
     */
    public void addMacSample(MacData macData){
        macSamples.add(macData);
    }

    /**
     * Adds a MAC sample to the dictionary
     * @param macMap
     */
    public void addMacSampleDict(HashMap<String, WifiObject> macMap){
        for(String key : macMap.keySet()){
            macSamples.add(new MacData(key, macMap.get(key).power));
        }
    }
}
