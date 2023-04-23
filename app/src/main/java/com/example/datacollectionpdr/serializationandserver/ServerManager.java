package com.example.datacollectionpdr.serializationandserver;


import com.example.datacollectionpdr.data.Trajectory;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.UserPositionData;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** ServerManager.java
 * Authors: Weston Everett
 * Affiliation: The University of Edinburgh
 * Description: Class containing static methods for reading/writing data from the server
 */

public class ServerManager {

    String apiKey;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String site = "https://openpositioning.org";

    /**
     * Sends data to the server
     * @param trajectoryNative data to serial/send
     * @param apiKey apikey to use
     * @return server response
     */
    public static String sendData(TrajectoryNative trajectoryNative, String apiKey) {

        OkHttpClient client = new OkHttpClient();
        // URL of the server
        String url =  site + "/api/live/trajectory/upload/"+ apiKey + "/?key=ewireless";

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
        } catch (IOException e) {
            return e.toString();
        }
    }

    /**
     * Pulls data from server
     * @return server response
     */
    public String downloadData(){
        OkHttpClient client = new OkHttpClient();

        String url = site + "/api/live/trajectory/download/" + apiKey + "/?key=ewireless";


        Request request = new Request.Builder()
                .url(url)
                .build();

        String responseBody;
        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
        } catch (IOException e) {
            responseBody = e.toString();
        }

        return responseBody;
    }

    /**
     * Reads available data from server
     * @return response string
     */
    public String readData(){
        OkHttpClient client = new OkHttpClient();
        // URL of the server
        String url = "https://openpositioning.org/api/live/users/trajectories/" + apiKey + "/?key=ewireless";


        Request request = new Request.Builder()
                .url(url)
                .build();

        String responseBody;
        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
        } catch (IOException e) {
            responseBody = e.toString();
        }

        return responseBody;
    }
}
