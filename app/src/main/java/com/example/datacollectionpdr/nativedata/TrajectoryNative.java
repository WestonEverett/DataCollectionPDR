package com.example.datacollectionpdr.nativedata;

import android.os.Build;

import com.example.datacollectionpdr.serializationandserver.TrajectoryBuilder;
import com.example.datacollectionpdr.data.Trajectory;

import java.util.ArrayList;

public class TrajectoryNative {

    long initTime;
    String androidVersion;
    String dataID;

    ArrayList<PDRStep> pdrs;
    ArrayList<APData> aps;
    ArrayList<GNSSData> gnssSamples;
    ArrayList<LightData> lights;
    ArrayList<WifiSample> wifis;
    ArrayList<MotionSample> motions;
    ArrayList<PositionData> positions;
    ArrayList<PressureData> baros;
    SensorDetails accInfo;
    SensorDetails gyroInfo;
    SensorDetails rotVectorInfo;
    SensorDetails magInfo;
    SensorDetails baroInfo;
    SensorDetails lightInfo;

    public TrajectoryNative(long initTime)
    {
        this.initTime = initTime;
        pdrs = new ArrayList<>();
        aps = new ArrayList<>();
        gnssSamples = new ArrayList<>();
        lights = new ArrayList<>();
        wifis = new ArrayList<>();
        motions = new ArrayList<>();
        positions = new ArrayList<>();
        baros = new ArrayList<>();
    }

    public TrajectoryNative(long initTime, String androidVersion, String dataID){
        this(initTime);
        this.androidVersion = String.valueOf(Build.VERSION.SDK_INT);
        this.dataID = dataID;
    }

    public void addPDRStep(PDRStep pdrStep){
        pdrs.add(pdrStep);
    }

    public void addAPData(APData apData){
        aps.add(apData);
    }

    public void addGNSS(GNSSData gnssData){
        gnssSamples.add(gnssData);
    }

    public void addLight(LightData lightData){
        lights.add(lightData);
    }

    public void addWifi(WifiSample wifiSample){
        wifis.add(wifiSample);
    }

    public void addMotion(MotionSample motionSample){
        motions.add(motionSample);
    }

    public void addPosition(PositionData positionData){
        positions.add(positionData);
    }

    public void addPressure(PressureData baro){
        baros.add(baro);
    }

    public void setAccInfo(SensorDetails sensorDetails){
        accInfo = sensorDetails;
    }

    public void setGyroInfo(SensorDetails sensorDetails){
        gyroInfo = sensorDetails;
    }

    public void setRotVectorInfo(SensorDetails sensorDetails){
        rotVectorInfo = sensorDetails;
    }

    public void setMagInfo(SensorDetails sensorDetails){
        magInfo = sensorDetails;
    }

    public void setBaroInfo(SensorDetails sensorDetails){
        baroInfo = sensorDetails;
    }

    public void setLightInfo(SensorDetails sensorDetails){
        lightInfo = sensorDetails;
    }

    public void setAndroidVersion(String version) { androidVersion = version; }

    public void setDataID(String dataID) { this.dataID = dataID; }

    public static class DistanceCalculator {

        private static final double EARTH_RADIUS = 6371000;

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
    }

    /* Trajectory correction function
       Takes two map points (latitudes and longitudes) provided by the user and computes the
       displacement of the user, and multiplies all the PDR step coordinates by the ratio of
       the magnitude the user provided to the magnitude calculated by the app.
       - Convert LatLon points to displacement magnitude
       - Multiply all PDR step points' x and y coordinates by userDistance/appDistance
     */
    public ArrayList<PDRStep> trajectoryCorrection(float startLon, float startLat, float endLon, float endLat, ArrayList<PDRStep> pdrs){
        ArrayList<PDRStep> newPDRs;
        newPDRs = pdrs;
        float startPointX, startPointY, endPointX, endPointY;
        float appDistance = 1;
        // Check to make sure PDR ArrayList is not empty or null
        if(pdrs != null && !pdrs.isEmpty()) {
            startPointX = pdrs.get(0).x;
            startPointY = pdrs.get(0).y;
            endPointX = pdrs.get(pdrs.size()-1).x;
            endPointY = pdrs.get(pdrs.size()-1).y;
            // Magnitude of PDR displacement using phone sensor data
            appDistance = (float) Math.sqrt((endPointX-startPointX)*(endPointX-startPointX)+(endPointY-startPointY)*(endPointY-startPointY));
        }
        // Magnitude of PDR displacement using user provided location pins
        float userDistance = (float) DistanceCalculator.calculateDistance(startLat,startLon,endLat,endLon);
        float ratio = userDistance/ appDistance;
        newPDRs.forEach(pdrStep -> pdrStep.x = pdrStep.x*ratio);
        newPDRs.forEach(pdrStep -> pdrStep.y = pdrStep.y*ratio);
        return newPDRs;
    }

    public Trajectory generateSerialized()
    {
        TrajectoryBuilder trajectoryBuilder = new TrajectoryBuilder(initTime, androidVersion, dataID);

        trajectoryBuilder.addAccInfo(accInfo);
        trajectoryBuilder.addGyroInfo(gyroInfo);
        trajectoryBuilder.addRotationVectorInfo(rotVectorInfo);
        trajectoryBuilder.addMagnetomerInfo(magInfo);
        trajectoryBuilder.addBaroInfo(baroInfo);
        trajectoryBuilder.addLightInfo(lightInfo);

        for(PDRStep pdr : pdrs){
            trajectoryBuilder.addPDR(pdr);
        }

        for(APData apData : aps){
            trajectoryBuilder.addAP(apData);
        }

        for(GNSSData gnssData : gnssSamples){
            trajectoryBuilder.addGNSS(gnssData);
        }

        for(LightData lightData : lights){
            trajectoryBuilder.addLight(lightData);
        }

        for(WifiSample wifiSample : wifis){
            trajectoryBuilder.addWifi(wifiSample);
        }

        for(MotionSample motionSample : motions){
            trajectoryBuilder.addMotion(motionSample);
        }

        for(PositionData positionData : positions){
            trajectoryBuilder.addPosition(positionData);
        }

        for(PressureData pressureData : baros){
            trajectoryBuilder.addBaro(pressureData);
        }

        return trajectoryBuilder.build();
    }
}
