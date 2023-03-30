package com.example.datacollectionpdr.serializationandserver;

import android.content.Context;
import android.util.Log;

import com.example.datacollectionpdr.nativedata.TrajectoryNative;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

    public static void createDataFile(Context context, TrajectoryNative trajectoryNative) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dataFileName = "Trajectory_" + timeStamp + ".pkt";
        File file = new File(context.getFilesDir(), dataFileName);

        try (FileOutputStream fos = context.openFileOutput(dataFileName, Context.MODE_PRIVATE)) {
            fos.write(trajectoryNative.generateSerialized().toByteArray());
        }

        Log.e("hm",dataFileName);
    }

}
