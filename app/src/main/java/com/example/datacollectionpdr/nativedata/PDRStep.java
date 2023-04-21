package com.example.datacollectionpdr.nativedata;

import android.hardware.SensorManager;
import android.util.Log;

import com.example.datacollectionpdr.pdrcalculation.DotProduct;
import com.example.datacollectionpdr.pdrcalculation.MadgwickAHRS;
import com.example.datacollectionpdr.pdrcalculation.StepLengthEstimation;

import java.util.ArrayList;
import java.util.Arrays;

/** PDRStep.java
 * Authors: Alexandros Miteloudis Vagionas, Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: PDRStep collects all relevant PDR data, namely step size, heading and altitude.
 * It calculates the displacement on an X-Y axis given a step size and heading.
 */
public class PDRStep {

    private float x; //X-axis displacement
    private float y; //Y-axis displacement
    private double heading; //Direction the user is facing with respect to North
    private int estFloor; //Estimated floor level the user is currently on
    private float magnitude; //Estimated step size
    public long initTime; //Time the step was taken

    /**
     * Constructs PDRStep from magnitude and direction
     * @param stepSize magnitude of change
     * @param heading direction of change
     * @param initTime time of change
     */
    public PDRStep(float stepSize, float heading, long initTime){
        this.heading = heading;
        this.magnitude = stepSize;
        Log.i("PDRStep", "stepSize: " + this.magnitude + "; Heading: " + this.heading);
        this.updateXY();
        this.initTime = initTime;
    }

    /**
     * Constructs PDRStep from x and y change
     * @param initTime time of change
     * @param x Change in x direction
     * @param y Change in y direction
     */
    public PDRStep(long initTime, float x, float y){
        this.x = x;
        this.y = y;
        this.heading = Math.atan2(this.y, this.x);
        this.magnitude = (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        this.initTime = initTime;
    }

    /**
     * Get X coordinate
     * @return
     */
    public float getX() {
        return x;
    }

    /**
     * Get Y coordinate
     * @return
     */
    public float getY() {
        return y;
    }

    /**
     * Get step magnitude
     * @return
     */
    public float getMagnitude() {
        return magnitude;
    }

    /**
     * updates magnitude to new value, updates X and Y given new heading
     */
    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
        this.updateXY();
    }

    /**
     * scales magnitude by a certain factor
     * @param ratio
     */
    public void scaleMagnitude(float ratio) {
        this.setMagnitude(ratio * this.magnitude);
    }

    public double getHeading() {
        return heading;
    }


    /**
     * changes heading, updates X and Y given new heading
     * @param heading new Heading
     */
    public void setHeading(double heading) {
        this.heading = heading;
        this.updateXY();
    }

    /**
     * Get estimated floor
     * @return
     */
    public int getEstFloor() {
        return estFloor;
    }

    /**
     * Set estimated floor
     * @param estFloor floor level
     */
    public void setEstFloor(int estFloor) {
        this.estFloor = estFloor;
    }

    /**
     * Updates the X-Y axis coordinates from the stored step size and heading
     */
    private void updateXY(){
        this.x = magnitude * (float) Math.sin(Math.toRadians(heading));
        this.y = magnitude * (float) Math.cos(Math.toRadians(heading));
    }

    /**
     * Updates the stored step size and heading from x and y
     */
    private void updateMagAndHeading(){
        this.magnitude = (float) Math.sqrt(x*x+y*y);
        this.heading = (float) Math.atan(y/x);
    }
}
