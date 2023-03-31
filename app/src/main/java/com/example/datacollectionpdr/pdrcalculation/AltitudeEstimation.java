package com.example.datacollectionpdr.pdrcalculation;

public class AltitudeEstimation {
    private static int FLOOR_HEIGHT = 4; // In metres
    private float altitude = 0.0f;
    private float startingAltitude;

    public void setAltitude(float altitude){
        this.altitude = altitude;
    }

    public void setStartingAltitude(float startingAltitude){
        this.startingAltitude = startingAltitude;
    }

    public float getAltitude(){
        return this.altitude;
    }

    public float getStartingAltitude(){
        return this.startingAltitude;
    }

    public int floorsChanged(){
        int numOfFloorsChanged = Math.floorDiv((int) (this.altitude - this.startingAltitude), FLOOR_HEIGHT);

        return numOfFloorsChanged;
    }

    public void findAltitude(float pressure){
        float altitudeChange = 0;
        altitudeChange = 44330*(float)Math.pow(1-(pressure/1013),-5.255);
        this.altitude += altitudeChange;
    }

}
