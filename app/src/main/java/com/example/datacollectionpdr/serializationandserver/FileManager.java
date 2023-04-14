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

public class FileManager {

    public static void createDataFile(Context context, TrajectoryNative trajectoryNative, String filename) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dataFileName = filename + "_" + timeStamp + ".pkt";
        File file = new File(context.getFilesDir(), dataFileName);

        try (FileOutputStream fos = context.openFileOutput(dataFileName, Context.MODE_PRIVATE)) {
            fos.write(trajectoryNative.generateSerialized().toByteArray());
            Toast.makeText(context, dataFileName + " saved", Toast.LENGTH_LONG);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "File not found", Toast.LENGTH_LONG);
        } catch (IOException e) {
            Toast.makeText(context, "Error while writing", Toast.LENGTH_LONG);
        }

        Log.e("hm",dataFileName);
    }

    public static String[] seeFiles(Context context){
        ArrayList<String> filenames = new ArrayList<>();
        for(String str : context.fileList()){
            if(str.contains(".pkt")){
                filenames.add(str);
            }
        }

        return filenames.toArray(new String[0]);
    }

    public static TrajectoryNative getTrajectoryFile(Context context, String filename) throws IOException {
        File file = new File(context.getFilesDir(), filename);
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        Trajectory loadedTraj = Trajectory.parseFrom(fileBytes);
        TrajectoryNative trajectoryNative = new TrajectoryNative(loadedTraj);


        return trajectoryNative;
    }

}
