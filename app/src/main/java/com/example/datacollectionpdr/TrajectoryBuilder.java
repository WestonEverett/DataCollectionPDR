package com.example.datacollectionpdr;

import com.example.datacollectionpdr.data.AP_Data;
import com.example.datacollectionpdr.data.GNSS_Sample;
import com.example.datacollectionpdr.data.Light_Sample;
import com.example.datacollectionpdr.data.Mac_Scan;
import com.example.datacollectionpdr.data.Motion_Sample;
import com.example.datacollectionpdr.data.Pdr_Sample;
import com.example.datacollectionpdr.data.Trajectory;

public class TrajectoryBuilder {

    long initTime;

    Trajectory.Builder trajectoryBuilder;

    public TrajectoryBuilder(long initTime){
        this.initTime = initTime;
        Trajectory.Builder trajectoryBuilder = Trajectory.newBuilder();
        trajectoryBuilder.setStartTimestamp(initTime);
    }

    public void addPDR(float x, float y, long curTime){
        Pdr_Sample.Builder newPDR = Pdr_Sample.newBuilder();
        newPDR.setRelativeTimestamp(curTime - initTime);
        newPDR.setX(x);
        newPDR.setY(y);

        trajectoryBuilder.addPdrData(newPDR.build());
    }

    public void addAP(long mac, String ssid, long freq){
        AP_Data.Builder newAP = AP_Data.newBuilder();
        newAP.setMac(mac);
        newAP.setSsid(ssid);
        newAP.setFrequency(freq);

        trajectoryBuilder.addApsData(newAP.build());
    }

    public void addGNSS(String provider, float acc, float alt, long curTime, float lon, float lat, float speed){
        GNSS_Sample.Builder newGNSS = GNSS_Sample.newBuilder();
        newGNSS.setAccuracy(acc);
        newGNSS.setAltitude(alt);
        newGNSS.setRelativeTimestamp(curTime - initTime);
        newGNSS.setLatitude(lat);
        newGNSS.setLongitude(lon);
        newGNSS.setProvider(provider);
        newGNSS.setSpeed(speed);

        trajectoryBuilder.addGnssData(newGNSS.build());
    }

    public void addLight(float light, long curTime){
        Light_Sample.Builder newLight = Light_Sample.newBuilder();
        newLight.setLight(light);
        newLight.setRelativeTimestamp(curTime- initTime);

        trajectoryBuilder.addLightData(newLight.build());
    }

    public void addMac(long curTime, long mac, int rssi){
        Mac_Scan.Builder newMac = Mac_Scan.newBuilder();
        newMac.setMac(mac);
        newMac.setRssi(rssi);
        newMac.setRelativeTimestamp(curTime - initTime);
    }

    public void addMotion(){
        Motion_Sample.Builder newMotion = Motion_Sample.newBuilder();
    }

}
