package com.example.datacollectionpdr.datacollectionandpreparation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class PermissionsManager extends AppCompatActivity{

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99; // What is this constant even?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermissions();
    }

    private void askPermissions(){
        if(android.os.Build.VERSION.SDK_INT >= 23){
            int wifiAccessPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE);
            int wifiChangePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CHANGE_WIFI_STATE);
            int coarseLocationPermission = ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            int activityRecognitionPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION);
            int internetPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);

            //If we don't have the permissions
            if(wifiAccessPermission != PackageManager.PERMISSION_GRANTED ||
                    wifiChangePermission != PackageManager.PERMISSION_GRANTED ||
                    coarseLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    activityRecognitionPermission != PackageManager.PERMISSION_GRANTED ||
                    internetPermission != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_WIFI_STATE,
                                android.Manifest.permission.CHANGE_WIFI_STATE,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACTIVITY_RECOGNITION,
                                android.Manifest.permission.INTERNET},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            }
        }
    }

    //When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_ID_READ_WRITE_PERMISSION: {
                //Note: If request is cancelled, the result arrays are empty
                if(grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[5] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_LONG).show();
                }
                //Cancelled or denied
                else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
