package com.example.datacollectionpdr;

import android.util.Log;

import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PDRStep;
import com.example.datacollectionpdr.nativedata.PressureData;

import java.util.ArrayList;
import java.util.Arrays;

public class RecentMeasurements {

    private final SetLengthLongArray motionTimeRecentMeasurements;
    private final SetLengthFloatArray[] accRecentMeasurements;
    private final SetLengthFloatArray[] gyroRecentMeasurements;
    private final SetLengthFloatArray[] rotRecentMeasurements;

    private final SetLengthLongArray baroTimeRecentMeasurements;
    private final SetLengthFloatArray[] baroRecentMeasurements;

    public RecentMeasurements(int graphPoints){
        motionTimeRecentMeasurements = new SetLengthLongArray(graphPoints);
        baroTimeRecentMeasurements = new SetLengthLongArray(graphPoints);
        accRecentMeasurements = new SetLengthFloatArray[]{
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
        };
        baroRecentMeasurements = new SetLengthFloatArray[]{
                new SetLengthFloatArray(graphPoints),
        };
        gyroRecentMeasurements = new SetLengthFloatArray[]{
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
        };
        rotRecentMeasurements = new SetLengthFloatArray[]{
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
                new SetLengthFloatArray(graphPoints),
        };
    }

    public void updateMeasurements(MotionSample motionSample){
        motionTimeRecentMeasurements.addValue(motionSample.initTime);

        float[] acc = motionSample.getAcc();

        accRecentMeasurements[0].addValue(acc[0]);
        accRecentMeasurements[1].addValue(acc[1]);
        accRecentMeasurements[2].addValue(acc[2]);

        float[] gyro = motionSample.getGyro();

        gyroRecentMeasurements[0].addValue(gyro[0]);
        gyroRecentMeasurements[1].addValue(gyro[1]);
        gyroRecentMeasurements[2].addValue(gyro[2]);

        float[] rot = motionSample.getRotVector();

        rotRecentMeasurements[0].addValue(rot[0]);
        rotRecentMeasurements[1].addValue(rot[1]);
        rotRecentMeasurements[2].addValue(rot[2]);
    }

    public void updateMeasurements(PressureData pressureData) {
        baroRecentMeasurements[0].addValue(pressureData.pressure);
        baroTimeRecentMeasurements.addValue(pressureData.timestamp);
    }

    public Number[][] getData(String sensor) {

        Long[] times = motionTimeRecentMeasurements.getArray();
        switch (sensor){
            case "Accelerometer":
                Float[] accX = accRecentMeasurements[0].getArray();
                Float[] accY = accRecentMeasurements[1].getArray();
                Float[] accZ = accRecentMeasurements[2].getArray();
                //Log.e("Hm", Arrays.toString(accX));
                return new Number[][]{times, accX, accY, accZ};
            case "Gyroscope":
                Float[] gyroX = gyroRecentMeasurements[0].getArray();
                Float[] gyroY = gyroRecentMeasurements[1].getArray();
                Float[] gyroZ = gyroRecentMeasurements[2].getArray();
                //Log.e("Hm", Arrays.toString(gyroX));
                return new Number[][]{times, gyroX, gyroY, gyroZ};
            case "Rotation":
                Float[] rotX = rotRecentMeasurements[0].getArray();
                Float[] rotY = rotRecentMeasurements[1].getArray();
                Float[] rotZ = rotRecentMeasurements[2].getArray();
                //Log.e("Hm", Arrays.toString(rotX));
                return new Number[][]{times, rotX, rotY, rotZ};
            case "Barometer":
                Long[] baroTimes = baroTimeRecentMeasurements.getArray();
                Float[] baro = baroRecentMeasurements[0].getArray();
                //Log.e("Hm", Arrays.toString(rotX));
                return new Number[][]{baroTimes, baro};
        }

        return new Number[][]{times};
    }
}
