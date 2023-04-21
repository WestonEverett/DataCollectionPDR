package com.example.datacollectionpdr.nativedata;

import android.os.Build;
import android.util.Log;

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

import kotlin.NotImplementedError;

/** TrajectoryNative.java
 * Authors: Weston Everett, Alexandros Miteloudis Vagionas
 * Affiliation: The University of Edinburgh
 * Description: A Native-Java Wrapper class for the Trajectory objects defined in the .proto files,
 *      provides functions for post-recording trajectory correction as well as serialization and
 *      deserialization to Trajectory objects
 *
 *      Several differences from Trajectory exist, such as PDRs, which are stored as relative to the
 *      last step, not relative to the start position for easier calculation
 */

public class TrajectoryNative {

    //Initial User-entered Position/Orientation
    UserPositionData initPos;

    //initial time
    public final long initTime;

    String androidVersion;
    String dataID;

    //PDR steps, each is a displacement from the last step
    ArrayList<PDRStep> pdrs;

    //WIFI data of the connected network
    ArrayList<APData> aps;

    //Location/GPS measurements
    ArrayList<GNSSData> gnssSamples;

    //Light measurements
    ArrayList<LightData> lights;

    //1-2 second scans of available WiFi networks and power
    ArrayList<WifiSample> wifis;

    //Accelo, Gyro, steps, and time measurements
    ArrayList<MotionSample> motions;

    //Magnetometer data (oddly named but following the Trajectory class for consistency)
    ArrayList<PositionData> positions;

    //Barometer/pressure measurements
    ArrayList<PressureData> baros;

    //Information about the sensors used to collect data
    SensorDetails accInfo;
    SensorDetails gyroInfo;
    SensorDetails rotVectorInfo;
    SensorDetails magInfo;
    SensorDetails baroInfo;
    SensorDetails lightInfo;

    /**
     *   Typical Constructor for building trajectories from scratch
     *   Requires initial Time and position
     */
    public TrajectoryNative(long initTime, UserPositionData initPos)
    {
        //initializes class variables
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

    /**
     *   Simple constructor Overload to automatically select start time
     */
    public TrajectoryNative(UserPositionData initPos) {
        this(System.currentTimeMillis(), initPos);
    }

    /**
     *   Constructor overload including data identifier
     */
    public TrajectoryNative(long initTime, UserPositionData initPos, String dataID){
        this(initTime, initPos);
        this.dataID = dataID;
    }

    /**
      *  Deserialization Constructor, accepts Trajectory object and creates a TrajectoryNative
      *  with the same internal data
     */
    public TrajectoryNative(Trajectory trajectory){

        //initializes and sets simple class variables
        this.initTime = trajectory.getStartTimestamp();
        this.androidVersion = trajectory.getAndroidVersion();
        this.dataID = trajectory.getDataIdentifier();

        this.accInfo = new SensorDetails(trajectory.getAccelerometerInfo());
        this.gyroInfo = new SensorDetails(trajectory.getGyroscopeInfo());
        this.rotVectorInfo = new SensorDetails(trajectory.getRotationVectorInfo());
        this.magInfo = new SensorDetails(trajectory.getMagnetometerInfo());
        this.baroInfo = new SensorDetails(trajectory.getBarometerInfo());
        this.lightInfo = new SensorDetails(trajectory.getLightSensorInfo());

        //Reads in PDR steps, including making X/Y relative to most recent step rather than start
        this.pdrs = new ArrayList<>();

        Pdr_Sample lastPDR = trajectory.getPdrDataList().get(0);
        for (Pdr_Sample pdr : trajectory.getPdrDataList()){
            pdrs.add(new PDRStep(
                    pdr.getRelativeTimestamp(),
                    (pdr.getX() - lastPDR.getX()),
                    (pdr.getY() - lastPDR.getY())
            ));

            lastPDR = pdr;
        }

        //removes initial (0,0) PDR
        pdrs.remove(0);

        //Reads in APS data
        this.aps = new ArrayList<>();
        for(AP_Data ap : trajectory.getApsDataList()){
            aps.add(new APData(ap.getMac(), ap.getSsid(), ap.getFrequency()));
        }

        //Reads in GPS/Position data
        this.gnssSamples = new ArrayList<>();
        for(GNSS_Sample gnss : trajectory.getGnssDataList()){
            gnssSamples.add(new GNSSData(gnss.getProvider(), gnss.getAccuracy(), gnss.getAltitude(), gnss.getRelativeTimestamp(), gnss.getLongitude(), gnss.getLatitude(), gnss.getSpeed()));
        }

        //Reads in light data
        this.lights = new ArrayList<>();
        for(Light_Sample light : trajectory.getLightDataList()){
            lights.add(new LightData(light.getRelativeTimestamp(), light.getLight()));
        }

        //Reads in wifi scans
        this.wifis = new ArrayList<>();
        for (WiFi_Sample wifi : trajectory.getWifiDataList()){
            WifiSample newWifi = new WifiSample(wifi.getRelativeTimestamp());
            for(Mac_Scan mac : wifi.getMacScansList()){
                newWifi.addMacSample(new MacData(mac.getMac(), mac.getRssi()));
            }
            wifis.add(newWifi);
        }

        //Reads in motion samples
        this.motions = new ArrayList<>();
        for(Motion_Sample mot : trajectory.getImuDataList()){
            MotionSample nativeMot = new MotionSample();
            nativeMot.setAcc(new float[]{mot.getAccX(), mot.getAccY(), mot.getAccZ()});
            nativeMot.setGyro(new float[]{mot.getGyrX(), mot.getGyrY(), mot.getGyrZ()});
            nativeMot.setRotVector(new float[]{mot.getRotationVectorX(), mot.getRotationVectorY(), mot.getRotationVectorZ(), mot.getRotationVectorW()});
            nativeMot.initTime = mot.getRelativeTimestamp();
            nativeMot.steps = mot.getStepCount();
            motions.add(nativeMot);
        }

        //reads in position (magnetometer) data
        this.positions = new ArrayList<>();
        for(Position_Sample pos : trajectory.getPositionDataList()){
            positions.add(new PositionData(pos.getRelativeTimestamp(), new float[]{pos.getMagX(), pos.getMagY(), pos.getMagZ()}));
        }

        //Reads in barometer/pressure data
        this.baros = new ArrayList<>();
        for(Pressure_Sample pressure : trajectory.getPressureDataList()){
            baros.add(new PressureData(pressure.getRelativeTimestamp(), pressure.getPressure()));
        }

        //Creates an initial location based on the GPS coordinates and the first PDR step
        if(pdrs.size() > 1 && gnssSamples.size() > 0){
            GNSSData gnssData = gnssSamples.get(0);
            PDRStep pdrStep = pdrs.get(1);
            /*
                Unit conversion of meters to lat/lon is not done as reference lat/lon is only used
                for direction so scale does not matter
             */
            this.initPos = new UserPositionData(gnssData.lat, gnssData.lon, gnssData.lat + pdrStep.getX(), gnssData.lon + pdrStep.getY());
        }
    }

    public ArrayList<PDRStep> getPdrs() {
        return pdrs;
    }

    public ArrayList<MotionSample> getMotionSample() {
        return motions;
    }

    /**
     * inserts new pdr to internal list after modify time to be relative
     * @param pdrStep PDR step object
     */
    public void addPDRStep(PDRStep pdrStep){
        pdrStep.initTime = pdrStep.initTime - this.initTime;
        pdrs.add(pdrStep);
    }

    /**
     * overload that does not modify time
     */
    public void addRawPDRStep(PDRStep pdrStep){
        pdrs.add(pdrStep);
    }

    /**
     * Adds AP data
     * @param apData AP data
     */
    public void addAPData(APData apData){
        aps.add(apData);
    }

    /**
     * Adds location data
     * @param gnssData location data
     */
    public void addGNSS(GNSSData gnssData){
        //converts absolute to relative time
        gnssData.initTime = gnssData.initTime - this.initTime;
        gnssSamples.add(gnssData);
    }

    /**
     * Adds ambient light data
     * @param lightData ambient light
     */
    public void addLight(LightData lightData){
        //converts absolute to relative time
        lightData.initTime = lightData.initTime - this.initTime;
        lights.add(lightData);
    }

    /**
     * Adds wifi data
     * @param wifiSample aggregated wifi data
     */
    public void addWifi(WifiSample wifiSample){
        //converts absolute to relative time
        wifiSample.initTime = wifiSample.initTime - this.initTime;
        wifis.add(wifiSample);
    }

    /**
     * Adds motion sample data
     * @param motionSample motion sample
     */
    public void addMotion(MotionSample motionSample){
        //converts absolute to relative time
        motionSample.initTime = motionSample.initTime - this.initTime;
        motions.add(motionSample);
    }

    /**
     * Adds position data
     * @param positionData position data
     */
    public void addPosition(PositionData positionData){
        //converts absolute to relative time
        positionData.initTime = positionData.initTime - this.initTime;
        positions.add(positionData);
    }

    /**
     * Adds pressure data
     * @param baro pressure data
     */
    public void addPressure(PressureData baro){
        //converts absolute to relative time
        baro.timestamp = baro.timestamp - this.initTime;
        baros.add(baro);
    }

    /**
     * Sets accelerometer information
     * @param sensorDetails
     */
    public void setAccInfo(SensorDetails sensorDetails){
        accInfo = sensorDetails;
    }

    /**
     * Sets gyroscope information
     * @param sensorDetails
     */
    public void setGyroInfo(SensorDetails sensorDetails){
        gyroInfo = sensorDetails;
    }

    /**
     * Sets rotation vector information
     * @param sensorDetails
     */
    public void setRotVectorInfo(SensorDetails sensorDetails){
        rotVectorInfo = sensorDetails;
    }

    /**
     * Sets magnetometer information
     * @param sensorDetails
     */
    public void setMagInfo(SensorDetails sensorDetails){
        magInfo = sensorDetails;
    }

    /**
     * Sets barometer information
     * @param sensorDetails
     */
    public void setBaroInfo(SensorDetails sensorDetails){
        baroInfo = sensorDetails;
    }

    /**
     * Sets ambient light sensor information
     * @param sensorDetails
     */
    public void setLightInfo(SensorDetails sensorDetails){
        lightInfo = sensorDetails;
    }

    /**
     * Sets data ID
     * @param dataID
     */
    public void setDataID(String dataID) { this.dataID = dataID; }

    /** Trajectory correction function
      * Takes two map points (latitudes and longitudes) provided by the user and computes the
      * displacement of the user, and multiplies all the PDR step coordinates by the ratio of
      * the magnitude the user provided to the magnitude calculated by the app.
      * - Convert LatLon points to displacement magnitude
      * - Multiply all PDR step points' x and y coordinates by userDistance/appDistance
     */

    public void applyTrajectoryScaling(UserPositionData endPos){
        //percentage the program is willing to scale by before assuming there is an error
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

    /**
     *   Uses the user-inputted locations/headings to calculate absolute heading change
     *   Assumes that this heading change is caused by a time-dependent drift
     *   Modifies each PDR step with a scaled heading change so the start and end headings are
     *       correct
     */
    public void applyGyroCorrection(UserPositionData endPos){

        double trueChangeInHeading = GNSSCalculations.userHeadingDeltaDeg(this.initPos, endPos);

        //Calculates the change in heading estimated by the PDR
        double estimatedChangeInHeading = 0;
        long totalTime = 1;

        if(this.pdrs.size() > 0){
            estimatedChangeInHeading = this.pdrs.get(pdrs.size()-1).getHeading() - this.pdrs.get(0).getHeading();
            totalTime = this.pdrs.get(pdrs.size()-1).initTime - this.initTime;
        }

        //Calculates difference to correct
        double changeInHeadingDif = trueChangeInHeading - estimatedChangeInHeading;

        /*
            Assumes heading drift is time-linear, so headingError = totalError * timePast/totalTime
            Adds calculated headingError to each pdrStep
         */
        for (PDRStep pdr: pdrs) {
            float percentTimePassed = ((float) (pdr.initTime - this.initTime)) / totalTime;
            double pdrHeadingDif = percentTimePassed * changeInHeadingDif;
            pdr.setHeading(pdr.getHeading() + pdrHeadingDif);
        }
    }

    public void applyAdditiveCorrection(UserPositionData endPos){

        double deltaX = GNSSCalculations.calculateDistance(
                (float) initPos.startLon,
                (float) initPos.startLat,
                (float) endPos.startLon,
                (float) initPos.startLat
        );

        double deltaY = GNSSCalculations.calculateDistance(
                (float) initPos.startLon,
                (float) initPos.startLat,
                (float) initPos.startLon,
                (float) endPos.startLat
        );

        float totalX = 0;
        float totalY = 0;

        if(pdrs != null && !pdrs.isEmpty()) {
            for(PDRStep pdrStep : pdrs){
                totalX = totalX + pdrStep.getX();
                totalY = totalY + pdrStep.getY();
            }
        }

        //Calculates difference to correct
        double errorX = (deltaX - totalX) / (float) pdrs.size();
        double errorY = (deltaY - totalY)  / (float) pdrs.size();

        for (PDRStep pdr: pdrs) {
            pdr.offsetX((float) errorX);
            pdr.offsetY((float) errorY);
        }
    }

    /**
       * Serializes the current TrajectoryNative object to a Trajectory object with TrajectoryBuilder
     */
    public Trajectory generateSerialized()
    {
        //Instantiates object of TrajectoryBuilder
        TrajectoryBuilder trajectoryBuilder = new TrajectoryBuilder(initTime, androidVersion, dataID);

        //adds sensor information
        trajectoryBuilder.addAccInfo(accInfo);
        trajectoryBuilder.addGyroInfo(gyroInfo);
        trajectoryBuilder.addRotationVectorInfo(rotVectorInfo);
        trajectoryBuilder.addMagnetomerInfo(magInfo);
        trajectoryBuilder.addBaroInfo(baroInfo);
        trajectoryBuilder.addLightInfo(lightInfo);

        //Translates
        PDRStep curPDR = new PDRStep(this.initTime, 0f, 0f);
        trajectoryBuilder.addPDR(curPDR);
        for(PDRStep pdr : pdrs){
            curPDR = new PDRStep(pdr.initTime, curPDR.getX() + pdr.getX(), curPDR.getY() + pdr.getY());
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
