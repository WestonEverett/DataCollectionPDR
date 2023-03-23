package com.example.datacollectionpdr;

import com.example.datacollectionpdr.data.AP_Data;
import com.example.datacollectionpdr.data.GNSS_Sample;
import com.example.datacollectionpdr.data.Light_Sample;
import com.example.datacollectionpdr.data.Mac_Scan;
import com.example.datacollectionpdr.data.Motion_Sample;
import com.example.datacollectionpdr.data.Pdr_Sample;
import com.example.datacollectionpdr.data.Position_Sample;
import com.example.datacollectionpdr.data.Pressure_Sample;
import com.example.datacollectionpdr.data.Sensor_Info;
import com.example.datacollectionpdr.data.Trajectory;
import com.example.datacollectionpdr.data.WiFi_Sample;

public class TrajectoryBuilder {

    long initTime;

    Trajectory.Builder trajectoryBuilder;

    public TrajectoryBuilder(long initTime){
        this.initTime = initTime;
        Trajectory.Builder trajectoryBuilder = Trajectory.newBuilder();
        trajectoryBuilder.setStartTimestamp(initTime);
    }

    public TrajectoryBuilder(long initTime, String androidVersion, String dataID){
        this(initTime);
        trajectoryBuilder.setAndroidVersion(androidVersion);
        trajectoryBuilder.setDataIdentifier(dataID);
    }

    public Trajectory build(){
        return trajectoryBuilder.build();
    }

    public void setDataID(String dataID){
        trajectoryBuilder.setDataIdentifier(dataID);
    }

    public void setAndroidVerion(String androidVersion){
        trajectoryBuilder.setAndroidVersion(androidVersion);
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

    public void addWifi(long curTime, long[] mac, int[] rssi){
        WiFi_Sample.Builder newWifi = WiFi_Sample.newBuilder();
        long relTime = curTime - initTime;

        newWifi.setRelativeTimestamp(relTime);

        for(int i = 0; i < mac.length; i++){
            Mac_Scan.Builder newMac = Mac_Scan.newBuilder();
            newMac.setMac(mac[i]);
            newMac.setRssi(rssi[i]);
            newMac.setRelativeTimestamp(relTime);

            newWifi.addMacScans(newMac.build());
        }

        trajectoryBuilder.addWifiData(newWifi.build());
    }

    public void addMotion(float[] acc, float[] gyro, float[] rotVector, int steps, long curTime){
        Motion_Sample.Builder newMotion = Motion_Sample.newBuilder();

        newMotion.setAccX(acc[0]);
        newMotion.setAccY(acc[1]);
        newMotion.setAccZ(acc[2]);

        newMotion.setGyrX(gyro[0]);
        newMotion.setGyrY(gyro[1]);
        newMotion.setGyrZ(gyro[2]);

        newMotion.setRotationVectorX(rotVector[0]);
        newMotion.setRotationVectorY(rotVector[1]);
        newMotion.setRotationVectorZ(rotVector[2]);
        newMotion.setRotationVectorW(rotVector[3]);

        newMotion.setStepCount(steps);
        newMotion.setRelativeTimestamp(curTime - initTime);

        trajectoryBuilder.addImuData(newMotion.build());
    }

    public void addPosition(long curTime, float[] mag){
        Position_Sample.Builder newPosition = Position_Sample.newBuilder();
        newPosition.setRelativeTimestamp(curTime - initTime);
        newPosition.setMagX(mag[0]);
        newPosition.setMagY(mag[1]);
        newPosition.setMagZ(mag[2]);

        trajectoryBuilder.addPositionData(newPosition.build());
    }

    public void addBaro(float pressure, long curTime){
        Pressure_Sample.Builder newPressure = Pressure_Sample.newBuilder();
        newPressure.setPressure(pressure);
        newPressure.setRelativeTimestamp(curTime - initTime);

        trajectoryBuilder.addPressureData(newPressure.build());
    }

    public void addAccInfo(String name, String vendor, float res, float power, int version, int type){
        Sensor_Info newSensor = createSensorInfo(name, vendor, res, power, version, type);

        trajectoryBuilder.setAccelerometerInfo(newSensor);
    }

    public void addGyroInfo(String name, String vendor, float res, float power, int version, int type){
        Sensor_Info newSensor = createSensorInfo(name, vendor, res, power, version, type);

        trajectoryBuilder.setGyroscopeInfo(newSensor);
    }

    public void addRotationVectorInfo(String name, String vendor, float res, float power, int version, int type){
        Sensor_Info newSensor = createSensorInfo(name, vendor, res, power, version, type);

        trajectoryBuilder.setRotationVectorInfo(newSensor);
    }

    public void addMagnetomerInfo(String name, String vendor, float res, float power, int version, int type){
        Sensor_Info newSensor = createSensorInfo(name, vendor, res, power, version, type);

        trajectoryBuilder.setMagnetometerInfo(newSensor);
    }

    public void addBaroInfo(String name, String vendor, float res, float power, int version, int type){
        Sensor_Info newSensor = createSensorInfo(name, vendor, res, power, version, type);

        trajectoryBuilder.setBarometerInfo(newSensor);
    }

    public void addLightInfo(String name, String vendor, float res, float power, int version, int type){
        Sensor_Info newSensor = createSensorInfo(name, vendor, res, power, version, type);

        trajectoryBuilder.setLightSensorInfo(newSensor);
    }

    private Sensor_Info createSensorInfo(String name, String vendor, float res, float power, int version, int type){
        Sensor_Info.Builder newSensor = Sensor_Info.newBuilder();
        newSensor.setName(name);
        newSensor.setVendor(vendor);
        newSensor.setResolution(res);
        newSensor.setPower(power);
        newSensor.setVersion(version);
        newSensor.setType(type);

        return newSensor.build();
    }


}
