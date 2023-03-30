package com.example.datacollectionpdr.serializationandserver;

import android.content.Context;
import android.util.Log;

import com.example.datacollectionpdr.data.Trajectory;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        String fileData = "Hello, world!";

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


        /*
        Log.e("serverManager", "Bytes serialized");


        // Create a URL object and open a connection
        URL obj = new URL(url);

        Log.e("serverManager", "URL Created");

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        Log.e("serverManager", "Connection Opened");

        // Set the request method to POST
        con.setRequestMethod("POST");

        // Set the content type and content length headers
        con.setRequestProperty("accept", "application/json");
        con.setRequestProperty("Content-Type", "multipart/form-data");
        con.setRequestProperty("file", "TrajectoryNewewewewewewe.pkt");

        // Enable output and disable caching
        con.setDoOutput(true);
        con.setUseCaches(false);

        serializedTraj.writeTo(con.getOutputStream());


        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        Log.e("serverManager", "Output Stream created");

        wr.write(trajBytes);
        wr.flush();
        wr.close();

        Log.e("serverManager", "Bytes Written");

        // Read the response from the server
        //BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        //String inputLine;
        //StringBuffer response = new StringBuffer();
        //while ((inputLine = in.readLine()) != null) {
        //    response.append(inputLine);
        //}
        //in.close();

        // Print the response from the server
        //Log.e("serverManager", "server Response: " + response.toString());

         */

    }

}
