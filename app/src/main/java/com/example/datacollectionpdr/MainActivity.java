package com.example.datacollectionpdr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity implements com.example.datacollectionpdr.DataCollection.OnMotionSensorManagerListener{

    private com.example.datacollectionpdr.DataCollection mMotionSensorManager;
    WifiManager wifiManager;
    String wifis[];
    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99; // What is this constant even?

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            unregisterReceiver(this);

            wifis = new String[wifiScanList.size()];
            Log.e("WiFi", String.valueOf(wifiScanList.size()));
            for(int i = 0; i<wifiScanList.size(); i++){
                wifis[i] = wifiScanList.get(i).SSID +
                        "," + wifiScanList.get(i).BSSID +
                        "," + String.valueOf(wifiScanList.get(i).level);
                Log.e("WiFi", String.valueOf(wifis[i]));
            }

            //lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,wifis));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMotionSensorManager = new com.example.datacollectionpdr.DataCollection(this);
        mMotionSensorManager.setOnMotionSensorManagerListener(this);


        askWiFiPermissions();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.getWifiState()==wifiManager.WIFI_STATE_DISABLED){
            wifiManager.setWifiEnabled(true);
        }

        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi..", Toast.LENGTH_SHORT).show();
    }

    private void askWiFiPermissions(){
        if(android.os.Build.VERSION.SDK_INT >= 23){
            int wifiAccessPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE);
            int wifiChangePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CHANGE_WIFI_STATE);
            int coarseLocationPermission = ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            //If we don't have the permissions
            if(wifiAccessPermission != PackageManager.PERMISSION_GRANTED ||
                    wifiChangePermission != PackageManager.PERMISSION_GRANTED ||
                    coarseLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    fineLocationPermission != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_WIFI_STATE,
                                android.Manifest.permission.CHANGE_WIFI_STATE,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                        grantResults[3] == PackageManager.PERMISSION_GRANTED){
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
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mMotionSensorManager.registerMotionSensors();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(wifiScanReceiver);
        mMotionSensorManager.unregisterMotionSensors();
    }

    @Override
    public void onAccValueUpdated(float[] acceleration) {
        //acc[0].setText("acc_Xaxis:" + acceleration[0]);
        //acc[1].setText("acc_Yaxis:" + acceleration[1]);
        //acc[2].setText("acc_Zaxis:" + acceleration[2]);
    }

    @Override
    public void onGyroValueUpdated(float[] gyroscope) {
        //gyro[0].setText("gyro_Xaxis:" + gyroscope[0]);
        //gyro[1].setText("gyro_Yaxis:" + gyroscope[1]);
        //gyro[2].setText("gyro_Zaxis:" + gyroscope[2]);
    }

    @Override
    public void onMagValueUpdated(float[] magneticfield, float h) {
        //mag[0].setText("mag_Xaxis:" + magneticfield[0]);
        //mag[1].setText("mag_Yaxis:" + magneticfield[1]);
        //mag[2].setText("mag_Zaxis:" + magneticfield[2]);
        //mag[3].setText("magnetic_field:" + h);
    }
}