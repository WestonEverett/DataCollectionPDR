package com.example.datacollectionpdr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.datacollectionpdr.nativedata.SensorDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class About extends AppCompatActivity {

    private SensorManager sensorManager; // SensorManager for getting sensor info (not data)
    Spinner dropdown;

    String[] SensorList = { "Accelerometer", "Gyroscope",
            "Magnetometer", "Barometer", "Ambient Light", "Proximity" };

    public static Map<String, Integer> sensorTypes;
    static {
        sensorTypes = new HashMap<>();
        sensorTypes.put("Accelerometer", Sensor.TYPE_ACCELEROMETER);
        sensorTypes.put("Gyroscope", Sensor.TYPE_GYROSCOPE);
        sensorTypes.put("Magnetometer", Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        sensorTypes.put("Barometer", Sensor.TYPE_PRESSURE);
        sensorTypes.put("Ambient Light", Sensor.TYPE_LIGHT);
        sensorTypes.put("Proximity", Sensor.TYPE_PROXIMITY);
    }

    TextView textViewSensorInfo;

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

        dropdown = findViewById(R.id.spinner_sensors);
        initspinnerfooter();

        //Simple TextView for sensor info display
        textViewSensorInfo = findViewById(R.id.textView_sensorInfo);
        textViewSensorInfo.setText(R.string.text_spiner_init);


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

    private void initspinnerfooter() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SensorList);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

                int currSensor = sensorTypes.get(parent.getItemAtPosition(position));
                SensorDetails currentDisplaySensor = sensorDetails(currSensor);
                textViewSensorInfo.setText(Html.fromHtml(
                        "<b>Name: </b>"+ currentDisplaySensor.name
                        +"<br> <b> Vendor: </b> " + currentDisplaySensor.vendor
                        +"<br> <b> Version: </b>"+currentDisplaySensor.version
                        +"<br> <b>Power:</b>  "+ currentDisplaySensor.power
                        +"<br><b>Resolution: </b> "+ currentDisplaySensor.res));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

}
