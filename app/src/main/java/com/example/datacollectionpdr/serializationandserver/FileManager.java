package com.example.datacollectionpdr.serializationandserver;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.datacollectionpdr.data.Trajectory;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.UserPositionData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Paths;

/** FileManager.java
 * Authors: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Class containing static methods for reading/writing data from files
 */

public class FileManager {

    /**
     * Serializes/saves a TrajectoryNative object to the relevant file location
     * @param context - Application Context
     * @param trajectoryNative - TrajectoryNative object to be saved
     * @param filename - filename to save under (which will be modified using the timestamp)
     */
    public static void createDataFile(Context context, TrajectoryNative trajectoryNative, String filename) {

        //Generates filename and file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dataFileName = filename + "_" + timeStamp + ".pkt";
        File file = new File(context.getFilesDir(), dataFileName);

        //Serializes trajectoryNative to Trajectory and writes as bytes to a file
        try (FileOutputStream fos = context.openFileOutput(dataFileName, Context.MODE_PRIVATE)) {
            fos.write(trajectoryNative.generateSerialized().toByteArray());
            Toast.makeText(context, dataFileName + " saved", Toast.LENGTH_LONG);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "File not found", Toast.LENGTH_LONG);
        } catch (IOException e) {
            Toast.makeText(context, "Error while writing", Toast.LENGTH_LONG);
        }
    }

    /**
     * Access the files saved in the local context
     * @param context - Application Context
     */
    public static String[] seeFiles(Context context){
        ArrayList<String> filenames = new ArrayList<>();
        for(String str : context.fileList()){
            //filters out any files that don't follow the .pkt format
            if(str.contains(".pkt")){
                filenames.add(str);
            }
        }

        return filenames.toArray(new String[0]);
    }

    /**
     * Loads/deserializes a saved Trajectory byte-file into a TrajectoryNative object
     * @param context - Application Context
     * @param filename - file to load
     */
    public static TrajectoryNative getTrajectoryFile(Context context, String filename) throws IOException {
        //Creates file object to read from
        File file = new File(context.getFilesDir(), filename);

        //Reads all bytes
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        //loads Trajectory object
        Trajectory loadedTraj = Trajectory.parseFrom(fileBytes);

        //deserializes Trajectory to TrajectoryNative
        TrajectoryNative trajectoryNative = new TrajectoryNative(loadedTraj);

        return trajectoryNative;
    }

    public static void deleteFile(Context context, String filename) throws IOException {
        //Creates file object to access
        File file = new File(context.getFilesDir(), filename);

        //Delete file
        file.delete();
    }

}
