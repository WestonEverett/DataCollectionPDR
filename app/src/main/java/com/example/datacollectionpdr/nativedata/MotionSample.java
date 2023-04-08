package com.example.datacollectionpdr.nativedata;

/** MotionSample.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object for holding timestamped accelerometer, gyroscope, rotation, and step count
 * data. Includes a flag for when a complete sample is acquired.
 */
public class MotionSample {

    public long initTime;

    private float[] acc;
    private boolean accFlag;

    private float[] gyro;
    private boolean gyroFlag;

    private float[] rotVector;
    private boolean rotVectorFlag;

    public int steps;


    public MotionSample() {
        this.accFlag = false;
        this.gyroFlag = false;
        this.rotVectorFlag = false;
    }

    public float[] getAcc() {
        return acc;
    }

    public void setAcc(float[] acc) {
        this.acc = acc;
        this.accFlag = true;
    }

    public float[] getGyro() {
        return gyro;
    }

    public void setGyro(float[] gyro) {
        this.gyro = gyro;
        this.gyroFlag = true;
    }

    public float[] getRotVector() {
        return rotVector;
    }

    public void setRotVector(float[] rotVector) {
        this.rotVector = rotVector;
        this.rotVectorFlag = true;
    }

    public boolean hasAcc() {
        return accFlag;
    }

    public boolean hasGyro() {
        return gyroFlag;
    }

    public boolean hasRotVector() {
        return rotVectorFlag;
    }

    public boolean isComplete() {
        return (gyroFlag && accFlag && rotVectorFlag);
    }
}
