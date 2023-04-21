package com.example.datacollectionpdr.pdrcalculation;

import com.example.datacollectionpdr.nativedata.UserPositionData;

/** GNSSCalculations.java
 * Author: Alexandros Miteloudis Vagionas
 * Affiliation: The University of Edinburgh
 * Description: Class for holding and processing location data. Used to find X-Y axis displacement
 * from user-provided location pins (which are longitude and latitude values, in degrees). In this
 * approximation, the Earth is assumed to be a perfect sphere.
 */

public class GNSSCalculations {
    private static final double EARTH_RADIUS = 6371000;

    //Included to accomodate UserPostionData class
    public static double calculateDistance(UserPositionData startLoc, UserPositionData endLoc){
        return calculateDistance((float) startLoc.startLon, (float) startLoc.startLat, (float) endLoc.startLon, (float) endLoc.startLat);
    }

    /**
     * Finds the straight-line distance between two map pins
     * @param startLon Starting Longitude
     * @param startLat Starting Latitude
     * @param endLon Ending Longitude
     * @param endLat Ending Latitude
     * @return
     */
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

    /**
     * Finds the angle in degrees with respect to North of the line crossing two map points.
     * @param startLon starting longitude
     * @param startLat starting latitude
     * @param endLon reference longitude
     * @param endLat reference latitude
     * @return final bearing
     */
    public static double calculateBearingDeg(double startLon, double startLat, double endLon, double endLat){
        /*     Let ‘R’ be the radius of Earth,
         *     ‘L’ be the longitude,
         *     ‘θ’ be latitude,
         *     ‘β‘ be Bearing.
         *     Adapted from https://www.igismap.com/formula-to-find-bearing-or-heading-angle-between-two-points-latitude-longitude/ */
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

    /**
     * Finds the change in angle between two map points, i.e. where the user was looking when they
     * started recording and where they were looking when they finished recording.
     * @param startPositionData Starting position/reference
     * @param endPositionData Ending position/referencce
     * @return change between the heading of the two values
     */
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
