package com.example.datacollectionpdr.nativedata;

import com.example.datacollectionpdr.pdrcalculation.GNSSCalculations;

public class UserPositionData {
    public final double startLat;
    public final double startLon;
    public final double startRefLat;
    public final double startRefLon;
    public final double heading;

    public UserPositionData(double startLat, double startLon, double startRefLat, double startRefLon){
        this.startLat = startLat;
        this.startLon = startLon;
        this.startRefLat = startRefLat;
        this.startRefLon = startRefLon;
        this.heading = GNSSCalculations.calculateBearingDeg(startLon, startLat, startRefLon, startRefLat);
    }

}
