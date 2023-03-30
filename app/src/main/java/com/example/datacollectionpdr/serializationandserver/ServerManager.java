package com.example.datacollectionpdr.serializationandserver;

import android.content.Context;
import android.util.Log;

import com.example.datacollectionpdr.data.Trajectory;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServerManager {

    String apiKey;

    public ServerManager(String apikey){
        this.apiKey = apikey;
    }

    public void sendData(TrajectoryNative trajectoryNative) throws Exception {
        // URL of the server
        String url = "https://openpositioning.org/api/dummy/trajectory/upload/";

        Trajectory serializedTraj = trajectoryNative.generateSerialized();
        byte[] trajBytes = serializedTraj.toByteArray();


        // Create a URL object and open a connection
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Set the request method to POST
        con.setRequestMethod("POST");

        // Set the content type and content length headers
        con.setRequestProperty("accept", "application/json");
        con.setRequestProperty("Content-Type", "multipart/form-data");

        // Enable output and disable caching
        con.setDoOutput(true);
        con.setUseCaches(false);

        serializedTraj.writeTo(con.getOutputStream());

        // Read the response from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the response from the server
        Log.e("Server Response", "server Response: " + response.toString());
    }

}
