package com.example.datacollectionpdr;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements com.example.datacollectionpdr.DataCollection.OnMotionSensorManagerListener{

    private com.example.datacollectionpdr.DataCollection mMotionSensorManager;

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMotionSensorManager = new com.example.datacollectionpdr.DataCollection(this);
        mMotionSensorManager.setOnMotionSensorManagerListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {

                if(grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMotionSensorManager.registerMotionSensors();
    }

    @Override
    protected void onPause(){
        super.onPause();

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