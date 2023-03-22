package com.example.datacollectionpdr;

public class PDREstimater {

    float[] acc;
    float[] angleChange;
    float[] mag;

    public PDREstimater(){ }

    public void updateAcc(float[] acc){
        this.acc = acc;
    }

    public void updateGyro(float[] gyro){
        this.angleChange = gyro;
    }

    public void updateMag(float[] mag){
        this.mag = mag;
    }

    public PDRStep calcStep() {
        return new PDRStep(0, 0);
    }
}