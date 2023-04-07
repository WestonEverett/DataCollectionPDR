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

    //Initialises internal values each time a step is taken
    public PDRStep(float stepSize, float heading, long initTime){
        this.heading = heading;
        this.magnitude = stepSize;
        Log.i("PDRStep", "stepSize:" + this.magnitude + "; Heading" + this.heading);
        this.updateXY();
        this.initTime = initTime;
    }

    //Can also be initialised with X-Y axis coordinates
    public PDRStep(long initTime, float x, float y){
        this.x = x;
        this.y = y;
        this.initTime = initTime;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
        this.updateXY();
    }

    public void scaleMagnitude(float ratio) {
        this.setMagnitude(ratio * this.magnitude);
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
        this.updateXY();
    }

    public int getEstFloor() {
        return estFloor;
    }

    public void setEstFloor(int estFloor) {
        this.estFloor = estFloor;
    }

    //Updates the X-Y axis coordinates from a step size and heading
    private void updateXY(){
        this.x = magnitude * (float) Math.sin(Math.toRadians(heading));
        this.y = magnitude * (float) Math.cos(Math.toRadians(heading));
    }

    //Added for completeness
    private void updateMagAndHeading(){
        this.magnitude = (float) Math.sqrt(x*x+y*y);
        this.heading = (float) Math.atan(y/x);
    }
}
