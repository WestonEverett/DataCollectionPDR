package com.example.datacollectionpdr.nativedata;

import android.os.Build;

import com.example.datacollectionpdr.data.AP_Data;
import com.example.datacollectionpdr.data.GNSS_Sample;
import com.example.datacollectionpdr.data.Light_Sample;
import com.example.datacollectionpdr.data.Mac_Scan;
import com.example.datacollectionpdr.data.Motion_Sample;
import com.example.datacollectionpdr.data.Pdr_Sample;
import com.example.datacollectionpdr.data.Position_Sample;
import com.example.datacollectionpdr.data.Pressure_Sample;
import com.example.datacollectionpdr.data.Sensor_Info;
import com.example.datacollectionpdr.data.WiFi_Sample;
import com.example.datacollectionpdr.pdrcalculation.GNSSCalculations;
import com.example.datacollectionpdr.serializationandserver.TrajectoryBuilder;
import com.example.datacollectionpdr.data.Trajectory;

import java.util.ArrayList;

/** TrajectoryNative.java
 * Authors: Weston Everett, Alexandros Miteloudis Vagionas, Dagna Wocjak
 * Affiliation: The University of Edinburgh
 * Description: TODO: FILL THIS IN
 */

public class TrajectoryNative {

    UserPositionData initPos;
    public final long initTime;
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

    public TrajectoryNative(Trajectory trajectory){
        this.initTime = trajectory.getStartTimestamp();
        this.androidVersion = trajectory.getAndroidVersion();
        this.dataID = trajectory.getDataIdentifier();

        this.accInfo = new SensorDetails(trajectory.getAccelerometerInfo());
        this.gyroInfo = new SensorDetails(trajectory.getGyroscopeInfo());
        this.rotVectorInfo = new SensorDetails(trajectory.getRotationVectorInfo());
        this.magInfo = new SensorDetails(trajectory.getMagnetometerInfo());
        this.baroInfo = new SensorDetails(trajectory.getBarometerInfo());
        this.lightInfo = new SensorDetails(trajectory.getLightSensorInfo());

        this.pdrs = new ArrayList<>();
        for (Pdr_Sample pdr : trajectory.getPdrDataList()){
            pdrs.add(new PDRStep(pdr.getRelativeTimestamp(), pdr.getX(), pdr.getY()));
        }

        this.aps = new ArrayList<>();
        for(AP_Data ap : trajectory.getApsDataList()){
            aps.add(new APData(ap.getMac(), ap.getSsid(), ap.getFrequency()));
        }

        this.gnssSamples = new ArrayList<>();
        for(GNSS_Sample gnss : trajectory.getGnssDataList()){
            gnssSamples.add(new GNSSData(gnss.getProvider(), gnss.getAccuracy(), gnss.getAltitude(), gnss.getRelativeTimestamp(), gnss.getLongitude(), gnss.getLatitude(), gnss.getSpeed()));
        }

        this.lights = new ArrayList<>();
        for(Light_Sample light : trajectory.getLightDataList()){
            lights.add(new LightData(light.getRelativeTimestamp(), light.getLight()));
        }

        this.wifis = new ArrayList<>();
        for (WiFi_Sample wifi : trajectory.getWifiDataList()){
            WifiSample newWifi = new WifiSample(wifi.getRelativeTimestamp());
            for(Mac_Scan mac : wifi.getMacScansList()){
                newWifi.addMacSample(new MacData(mac.getMac(), mac.getRssi()));
            }
            wifis.add(newWifi);
        }

        this.motions = new ArrayList<>();
        for(Motion_Sample mot : trajectory.getImuDataList()){
            MotionSample nativeMot = new MotionSample();
            nativeMot.setAcc(new float[]{mot.getAccX(), mot.getAccY(), mot.getAccZ()});
            nativeMot.setGyro(new float[]{mot.getGyrX(), mot.getGyrY(), mot.getGyrZ()});
            nativeMot.setRotVector(new float[]{mot.getRotationVectorX(), mot.getRotationVectorY(), mot.getRotationVectorZ(), mot.getRotationVectorW()});
            nativeMot.initTime = mot.getRelativeTimestamp();
            nativeMot.steps = 0;
            motions.add(nativeMot);
        }

        this.positions = new ArrayList<>();
        for(Position_Sample pos : trajectory.getPositionDataList()){
            positions.add(new PositionData(pos.getRelativeTimestamp(), new float[]{pos.getMagX(), pos.getMagY(), pos.getMagZ()}));
        }

        this.baros = new ArrayList<>();
        for(Pressure_Sample pressure : trajectory.getPressureDataList()){
            baros.add(new PressureData(pressure.getRelativeTimestamp(), pressure.getPressure()));
        }

        if(pdrs.size() > 1 && gnssSamples.size() > 0){
            GNSSData gnssData = gnssSamples.get(0);
            PDRStep pdrStep = pdrs.get(1);
            this.initPos = new UserPositionData(gnssData.lat, gnssData.lon, gnssData.lat + pdrStep.getX(), gnssData.lon + pdrStep.getY());
        }
    }

    public ArrayList<PDRStep> getPdrs() {
        return pdrs;
    }

    public ArrayList<MotionSample> getMotionSample() {
        return motions;
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

    public void applyTrajectoryScaling(UserPositionData endPos){
        final float RATIO_LIMIT = 0.25f;

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
        float userDistance = (float) GNSSCalculations.calculateDistance(this.initPos,endPos);
        float ratio = userDistance/ appDistance;
        if(ratio > (1+RATIO_LIMIT)*ratio || ratio < (1-RATIO_LIMIT)*ratio) {
            pdrs.forEach(pdrStep -> pdrStep.scaleMagnitude(ratio));
        }
    }

    public void applyGyroCorrection(UserPositionData endPos){

        double trueChangeInHeading = GNSSCalculations.userHeadingDeltaDeg(this.initPos, endPos);

        double estimatedChangeInHeading = 0;
        long totalTime = 1;

        if(this.pdrs.size() > 0){
            estimatedChangeInHeading = this.pdrs.get(pdrs.size()-1).getHeading() - this.pdrs.get(0).getHeading();
            totalTime = this.pdrs.get(pdrs.size()-1).initTime - this.initTime;
        }

        double changeInHeadingDif = trueChangeInHeading - estimatedChangeInHeading;

        for (PDRStep pdr: pdrs) {
            float percentTimePassed = ((float) (pdr.initTime - this.initTime)) / totalTime;
            double pdrHeadingDif = percentTimePassed * changeInHeadingDif;
            pdr.setHeading(pdr.getHeading() + pdrHeadingDif);
        }
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

        PDRStep curPDR = new PDRStep(this.initTime, 0f, 0f);
        trajectoryBuilder.addPDR(curPDR);

        for(PDRStep pdr : pdrs){
            curPDR = new PDRStep(this.initTime, curPDR.getX() + pdr.getX(), curPDR.getY() + pdr.getY());
            trajectoryBuilder.addPDR(curPDR);
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
