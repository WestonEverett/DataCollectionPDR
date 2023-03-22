package com.example.datacollectionpdr;

public class PDREstimater {

    float lastBaro;
    float curBaro;
    float[] acc;
    float[] angleChange;
    float[] mag;

    public PDREstimater(float baro){
        this.lastBaro = baro;
    }

    public void updateAcc(float[] acc){
        this.acc = acc;
    }

    public void updateGyro(float[] gyro){
        this.angleChange = gyro;
    }

    public void updateMag(float[] mag){
        this.mag = mag;
    }

    public void updateBaro(float baro){
        this.curBaro = baro;
    }

    public PDRStep calcStep() {
        return new PDRStep(0, 0, 0);
    }
}