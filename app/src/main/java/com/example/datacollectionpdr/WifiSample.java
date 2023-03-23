package com.example.datacollectionpdr;

import java.util.ArrayList;
import java.util.HashMap;

public class WifiSample {

    public long initTime;
    public ArrayList<MacData> macSamples;

    public WifiSample(long initTime){
        this.initTime = initTime;
        this.macSamples = new ArrayList<MacData>();
    }

    public void addMacSample(MacData macData){
        macSamples.add(macData);
    }

    public void addMacSampleDict(HashMap<String, Integer> macMap){
        for(String key : macMap.keySet()){
            macSamples.add(new MacData(key, macMap.get(key)));
        }
    }
}
