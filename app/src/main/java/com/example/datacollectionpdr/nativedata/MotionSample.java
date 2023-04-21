package com.example.datacollectionpdr.nativedata;

/** MotionSample.java
 * Author: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Object for holding timestamped accelerometer, gyroscope, rotation, and step count
 * data. Includes a flag for when a complete sample is acquired.
 */
public class MotionSample {

    public long initTime;

    //acceleration data and presence boolean
    private float[] acc;
    private boolean accFlag;

    //gyroscope data and presence
    private float[] gyro;
    private boolean gyroFlag;

    //rotation vector data and presence
    private float[] rotVector;
    private boolean rotVectorFlag;

    //number of steps
    public int steps;

    /**
     * MotionSample Constructor, starts with no data
     */
    public MotionSample() {
        this.accFlag = false;
        this.gyroFlag = false;
        this.rotVectorFlag = false;
    }

    /**
     * Returns the acceleration value
     * @return
     */
    public float[] getAcc() {
        return acc;
    }

    /**
     * Sets the acceleration value
     * @param acc acceleration
     */
    public void setAcc(float[] acc) {
        this.acc = acc;
        this.accFlag = true;
    }

    /**
     * Returns the gyroscope value
     * @return
     */
    public float[] getGyro() {
        return gyro;
    }

    /**
     * Sets the gyroscope value
     * @param gyro gyroscope
     */
    public void setGyro(float[] gyro) {
        this.gyro = gyro;
        this.gyroFlag = true;
    }

    /**
     * Returns the rotation vector
     * @return
     */
    public float[] getRotVector() {
        return rotVector;
    }

    /**
     * Sets the rotation vector
     * @param rotVector rotation vector
     */
    public void setRotVector(float[] rotVector) {
        this.rotVector = rotVector;
        this.rotVectorFlag = true;
    }

    /**
     * Determines if the sample has an accuracy value saved
     * @return
     */
    public boolean hasAcc() {
        return accFlag;
    }

    /**
     * Determines if the sample has a gyroscope value saved
     * @return
     */
    public boolean hasGyro() {
        return gyroFlag;
    }

    /**
     * Determines if the sample has a rotation vector value saved
     * @return
     */
    public boolean hasRotVector() {
        return rotVectorFlag;
    }

    /**
     * Determines if the sample has all values saved
     * @return
     */
    public boolean isComplete() {
        return (gyroFlag && accFlag && rotVectorFlag);
    }
}
