package com.example.datacollectionpdr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.datacollectionpdr.nativedata.SensorDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class About extends AppCompatActivity {

    private SensorManager sensorManager; // SensorManager for getting sensor info (not data)

    // Function returns sensor info object
    public SensorDetails sensorDetails(int type){
        String name = sensorManager.getDefaultSensor(type).getName();
        String vendor = sensorManager.getDefaultSensor(type).getVendor();
        float res = sensorManager.getDefaultSensor(type).getResolution();
        float power = sensorManager.getDefaultSensor(type).getPower();
        int version = sensorManager.getDefaultSensor(type).getVersion();
        return new SensorDetails(name, vendor,res,power,version,type);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setContentView(R.layout.activity_about);

        getSupportActionBar().hide();

        //Simple TextView for accelerometer info, change into menu later
        TextView textView9 = findViewById(R.id.text_sensor_info1);
        SensorDetails Accelerometer = sensorDetails(Sensor.TYPE_ACCELEROMETER); // Instantiate SensorDetails object with the sensor type
        textView9.setText(Accelerometer.name+" "+Accelerometer.vendor+" "+Accelerometer.version);
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.about);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),DashBoard.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.about:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
    public static final String EXTRA_MESSAGE = "com.example.datacollectionPDR.MESSAGE";

}
