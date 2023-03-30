package com.example.datacollectionpdr.serializationandserver;

import android.content.Context;
import android.util.Log;

import com.example.datacollectionpdr.data.Trajectory;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ServerManager {

    String apiKey;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public ServerManager(String apikey){
        this.apiKey = apikey;
    }

    public String sendData(TrajectoryNative trajectoryNative) throws IOException {

        OkHttpClient client = new OkHttpClient();
        // URL of the server
        String url = "https://openpositioning.org/api/live/trajectory/upload/" + apiKey + "/?key=ewireless";

        Trajectory serializedTraj = trajectoryNative.generateSerialized();
        byte[] trajBytes = serializedTraj.toByteArray();

        String fileName = "example.pkt";
        String fileType = "application/json";

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(trajBytes, MediaType.parse(fileType)))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public TrajectoryNative getData(){
        return new TrajectoryNative(12);
    }
}
