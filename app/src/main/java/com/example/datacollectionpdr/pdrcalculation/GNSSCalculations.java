package com.example.datacollectionpdr.pdrcalculation;

import com.example.datacollectionpdr.nativedata.UserPositionData;

public class GNSSCalculations {
    private static final double EARTH_RADIUS = 6371000;

    public static double calculateDistance(UserPositionData startLoc, UserPositionData endLoc){
        return calculateDistance((float) startLoc.startLon, (float) startLoc.startLat, (float) endLoc.startLon, (float) endLoc.startLat);
    }

    public static double calculateDistance(float startLon, float startLat, float endLon, float endLat) {
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLon - startLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return distance;
    }

    public static double calculateBearingDeg(double startLon, double startLat, double endLon, double endLat){
        /*     Let ‘R’ be the radius of Earth,
         *     ‘L’ be the longitude,
         *     ‘θ’ be latitude,
         *     ‘β‘ be Bearing. */
        double sLn = Math.toRadians(startLon);
        double sLt = Math.toRadians(startLat);
        double eLn = Math.toRadians(endLon);
        double eLt = Math.toRadians(endLat);

        double bearing;
        double X = Math.cos(eLt) * Math.sin(eLn-sLn); // X = cos θb * sin ∆L
        double Y = Math.cos(sLt)*Math.sin(eLt)-Math.sin(sLt)*Math.cos(eLt)*Math.cos(eLn-sLn); // Y = cos θa * sin θb – sin θa * cos θb * cos ∆L
        bearing = Math.toDegrees(Math.atan2(X,Y)); // β = atan2(X,Y)

        return bearing;
    }

    public static double userHeadingDeltaDeg(UserPositionData startPositionData, UserPositionData endPositionData){
        double baseHeading = endPositionData.heading - startPositionData.heading;

        while(baseHeading < -180){
            baseHeading += 360;
        }

        while(baseHeading >= 180){
            baseHeading -= 360;
        }

        return baseHeading;
    }
}
