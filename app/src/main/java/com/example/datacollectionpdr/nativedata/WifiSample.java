package com.example.datacollectionpdr.nativedata;

import com.example.datacollectionpdr.datacollectionandpreparation.WifiObject;

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

    public void addMacSampleDict(HashMap<String, WifiObject> macMap){
        for(String key : macMap.keySet()){
            macSamples.add(new MacData(key, macMap.get(key).power));
        }
    }
}
