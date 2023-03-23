package com.example.datacollectionpdr;

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
    }

    public TrajectoryNative(long initTime, String androidVersion, String dataID){
        this(initTime);
        this.androidVersion = androidVersion;
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
