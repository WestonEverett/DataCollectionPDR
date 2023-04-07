package com.example.datacollectionpdr.nativedata;

import android.hardware.SensorManager;
import android.util.Log;

import com.example.datacollectionpdr.pdrcalculation.DotProduct;
import com.example.datacollectionpdr.pdrcalculation.MadgwickAHRS;
import com.example.datacollectionpdr.pdrcalculation.StepLengthEstimation;

import java.util.ArrayList;
import java.util.Arrays;

public class PDRStep {

    private float x;
    private float y;
    private double heading;
    private int estFloor;
    private float magnitude;
    public long initTime;

    public PDRStep(float stepSize, float heading, long initTime){
        ////// Finding heading //////
        this.heading = heading;
        this.magnitude = stepSize;
        Log.i("PDRStep", "stepSize:" + this.magnitude + "; Heading" + this.heading);
        this.updateXY();
        ////// Finding x and y lengths //////
        this.initTime = initTime;
    }

    public PDRStep(long initTime, float x, float y){
        this.x = x;
        this.y = y;
        this.heading = Math.atan2(this.y, this.x);
        this.magnitude = (float) Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
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

    private void updateXY(){
        this.x = magnitude * (float) Math.sin(Math.toRadians(heading));
        this.y = magnitude * (float) Math.cos(Math.toRadians(heading));
    }
}
