package com.example.datacollectionpdr.nativedata;

import android.os.Build;

import com.example.datacollectionpdr.pdrcalculation.GNSSCalculations;
import com.example.datacollectionpdr.serializationandserver.TrajectoryBuilder;
import com.example.datacollectionpdr.data.Trajectory;

import java.util.ArrayList;

public class TrajectoryNative {

    UserPositionData initPos;
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

    public TrajectoryNative(long initTime, UserPositionData initPos)
    {
        this.initTime = initTime;
        this.initPos = initPos;
        this.androidVersion = String.valueOf(Build.VERSION.SDK_INT);
        pdrs = new ArrayList<>();
        aps = new ArrayList<>();
        gnssSamples = new ArrayList<>();
        lights = new ArrayList<>();
        wifis = new ArrayList<>();
        motions = new ArrayList<>();
        positions = new ArrayList<>();
        baros = new ArrayList<>();
    }

    public TrajectoryNative(long initTime, UserPositionData initPos, String androidVersion, String dataID){
        this(initTime, initPos);
        this.androidVersion = androidVersion;
        this.dataID = dataID;
    }

    public ArrayList<PDRStep> getPdrs() {
        return pdrs;
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

    public void setDataID(String dataID) { this.dataID = dataID; }

    /* Trajectory correction function
       Takes two map points (latitudes and longitudes) provided by the user and computes the
       displacement of the user, and multiplies all the PDR step coordinates by the ratio of
       the magnitude the user provided to the magnitude calculated by the app.
       - Convert LatLon points to displacement magnitude
       - Multiply all PDR step points' x and y coordinates by userDistance/appDistance
     */

    public void applyTrajectoryScaling(float startLon, float startLat, float endLon, float endLat){

        //TODO Add Bounds, if each step is going to be changed by more than +- 25 (ish) make no change as we are in a boundary condition
        //Should be done ^^
        final float RATIO_LIMIT = 0.25f;

        ArrayList<PDRStep> newPDRs = this.pdrs;
        float startPointX, startPointY, endPointX, endPointY;
        float appDistance = 1;
        // Check to make sure PDR ArrayList is not empty or null

        if(pdrs != null && !pdrs.isEmpty()) {
            float totalX = 0;
            float totalY = 0;

            for(PDRStep pdrStep : pdrs){
                totalX = totalX + pdrStep.getX();
                totalY = totalY + pdrStep.getY();
            }
            // Magnitude of PDR displacement using phone sensor data
            appDistance = (float) Math.sqrt((totalX*totalX)+(totalY*totalY));
        }
        // Magnitude of PDR displacement using user provided location pins
        float userDistance = (float) GNSSCalculations.calculateDistance(startLat,startLon,endLat,endLon);
        float ratio = userDistance/ appDistance;
        if(ratio > (1+RATIO_LIMIT)*ratio || ratio < (1-RATIO_LIMIT)*ratio) {
            newPDRs.forEach(pdrStep -> pdrStep.scaleMagnitude(ratio));
        }
        this.pdrs = newPDRs;
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
