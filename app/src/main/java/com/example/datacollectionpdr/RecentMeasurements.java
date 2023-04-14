package com.example.datacollectionpdr;

import com.example.datacollectionpdr.nativedata.MotionSample;
import com.example.datacollectionpdr.nativedata.PressureData;

/**
 * Class that manages sensor data and structures them into formats used in the UI visualisation tools
 * such as XYplots
 */
public class RecentMeasurements {

    private final SetLengthLongArray motionTimeRecentMeasurements;  //initialise array to store motion samples
    private final SetLengthFloatArray[] accRecentMeasurements;  //initialise array to store accelerometer samples
    private final SetLengthFloatArray[] gyroRecentMeasurements;  //initialise array to store gyro samples
    private final SetLengthFloatArray[] rotRecentMeasurements;  //initialise array to store rotation samples
    private final SetLengthLongArray baroTimeRecentMeasurements;//initialise array to store times of barometer samples
    private final SetLengthFloatArray[] baroRecentMeasurements;//initialise array to store barometer samples

    /**
     * Function that puts a set number of past sensor measurements into arrays so that they can easily be plotted
     * @param graphPoints - number of samples in the arrys
     */
    public RecentMeasurements(int graphPoints){

        //set array sizes based on input
        motionTimeRecentMeasurements = new SetLengthLongArray(graphPoints); //motion sensor
        baroTimeRecentMeasurements = new SetLengthLongArray(graphPoints);   //barometer  time
        accRecentMeasurements = new SetLengthFloatArray[]{                  //accelerometer
                new SetLengthFloatArray(graphPoints),                           //x
                new SetLengthFloatArray(graphPoints),                           //y
                new SetLengthFloatArray(graphPoints),                           //z
        };
        baroRecentMeasurements = new SetLengthFloatArray[]{                 //barometer
                new SetLengthFloatArray(graphPoints),
        };
        gyroRecentMeasurements = new SetLengthFloatArray[]{                  //gyroscope
                new SetLengthFloatArray(graphPoints),                            // roll
                new SetLengthFloatArray(graphPoints),                            // yaw
                new SetLengthFloatArray(graphPoints),                            // pitch
        };
        rotRecentMeasurements = new SetLengthFloatArray[]{                   //rotation sensor
                new SetLengthFloatArray(graphPoints),                            // roll
                new SetLengthFloatArray(graphPoints),                            // yaw
                new SetLengthFloatArray(graphPoints),                            // pitch
        };
    }

    /**
     * Update the measurements when new motion sample is ready
     * @param motionSample  - MotionSample containing accelerometer, gyro, magnet, step count, and
     *                      time readings
     */
    public void updateMeasurements(MotionSample motionSample){
        motionTimeRecentMeasurements.addValue(motionSample.initTime); //time for the last motion sample

        float[] acc = motionSample.getAcc();   //acceleration

        accRecentMeasurements[0].addValue(acc[0]);  //x
        accRecentMeasurements[1].addValue(acc[1]);  //y
        accRecentMeasurements[2].addValue(acc[2]);  //z

        float[] gyro = motionSample.getGyro();   //gyroscope

        gyroRecentMeasurements[0].addValue(gyro[0]);  // roll
        gyroRecentMeasurements[1].addValue(gyro[1]);  // yaw
        gyroRecentMeasurements[2].addValue(gyro[2]);  // pitch

        float[] rot = motionSample.getRotVector(); //rotation

        rotRecentMeasurements[0].addValue(rot[0]);   // roll
        rotRecentMeasurements[1].addValue(rot[1]);   // yaw
        rotRecentMeasurements[2].addValue(rot[2]);   // pitch
    }

    /**
     * Update the measurements when new motion sample is ready
     * @param pressureData  - Object containing the most recent barometer reading and timestamp
     */
    public void updateMeasurements(PressureData pressureData) {
        baroRecentMeasurements[0].addValue(pressureData.pressure);  //get reading
        baroTimeRecentMeasurements.addValue(pressureData.timestamp);//get timestamp
    }

    /**
     * function that gives array containing time and data of the last x samples of
     * a chosen sensor
     * @param sensor - which sensor is to be plotted
     * @return - array with recent samples of that sensor (Floats) and timestamps (Longs)
     */
    public Number[][] getData(String sensor) {

        Long[] times = motionTimeRecentMeasurements.getArray();     //time from motion measurements
        switch (sensor){
            case "Accelerometer":
                Float[] accX = accRecentMeasurements[0].getArray();    //x
                Float[] accY = accRecentMeasurements[1].getArray();    //y
                Float[] accZ = accRecentMeasurements[2].getArray();    //z
                //Log.e("Hm", Arrays.toString(accX));
                return new Number[][]{times, accX, accY, accZ};
            case "Gyroscope":
                Float[] gyroX = gyroRecentMeasurements[0].getArray();  // roll
                Float[] gyroY = gyroRecentMeasurements[1].getArray();  // yaw
                Float[] gyroZ = gyroRecentMeasurements[2].getArray();  // pitch
                //Log.e("Hm", Arrays.toString(gyroX));
                return new Number[][]{times, gyroX, gyroY, gyroZ};
            case "Rotation":
                Float[] rotX = rotRecentMeasurements[0].getArray();   // roll
                Float[] rotY = rotRecentMeasurements[1].getArray();   // yaw
                Float[] rotZ = rotRecentMeasurements[2].getArray();   // pitch
                //Log.e("Hm", Arrays.toString(rotX));
                return new Number[][]{times, rotX, rotY, rotZ};
            case "Barometer":
                Long[] baroTimes = baroTimeRecentMeasurements.getArray();   //timstamps from barometer
                Float[] baro = baroRecentMeasurements[0].getArray();     //barometer
                //Log.e("Hm", Arrays.toString(rotX));
                return new Number[][]{baroTimes, baro};
        }

        return new Number[][]{times};   //if different sensor selected return array with just timestamps
    }
}
