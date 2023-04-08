package com.example.datacollectionpdr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datacollectionpdr.nativedata.SensorDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class About extends AppCompatActivity {

    Spinner dropdown;//Dropdown view
    TextView textViewSensorInfo;    //Text View for info display

    private SensorManager sensorManager; // SensorManager for getting sensor info (not data)


    public static Map<String, Integer> sensorTypes; //Hashmap to map dropdown menu options to sensorTypes objects
    static {
        sensorTypes = new HashMap<>();
        sensorTypes.put("Accelerometer", Sensor.TYPE_ACCELEROMETER);
        sensorTypes.put("Gyroscope", Sensor.TYPE_GYROSCOPE);
        sensorTypes.put("Magnetometer", Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        sensorTypes.put("Barometer", Sensor.TYPE_PRESSURE);
        sensorTypes.put("Ambient Light", Sensor.TYPE_LIGHT);
        sensorTypes.put("Proximity", Sensor.TYPE_PROXIMITY);
    }
    String[] sensorList = { "Accelerometer", "Gyroscope",
            "Magnetometer", "Barometer", "Ambient Light", "Proximity" }; //Options for the dropdown - sensor list

    List<String> sensorListDropDown=new ArrayList<String>();


    /** Function returns sensor info object to display sensor details
     * input: Sensor type as a public static int
     * output: SensorDetails object with sensor information for display**/
    public SensorDetails sensorDetails(int type){
        String name = sensorManager.getDefaultSensor(type).getName();       //Name
        String vendor = sensorManager.getDefaultSensor(type).getVendor();   //Vendor
        float res = sensorManager.getDefaultSensor(type).getResolution();   //Resolution
        float power = sensorManager.getDefaultSensor(type).getPower();      //Power
        int version = sensorManager.getDefaultSensor(type).getVersion();    // Sensor Version
        return new SensorDetails(name, vendor,res,power,version,type);
    }

    /** Function setting up view of the activity
     * input: state of the activity
     * output: - **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //Set up sensor manager
        setContentView(R.layout.activity_about);        //activity view from xml
        Objects.requireNonNull(getSupportActionBar()).hide();                   // Hide app Bar

        for (String element : sensorList){
            if(sensorManager.getDefaultSensor(sensorTypes.get(element)) != null){
                //int currSensor = sensorTypes.get(element); //get sensor type based on the hashmap and item selected
                sensorListDropDown.add(element);
            }
        }

        dropdown = findViewById(R.id.spinner_sensors);  //find and initialise the dropdown menu
        initspinnerfooter();

        //Simple TextView for sensor info display
        textViewSensorInfo = findViewById(R.id.textView_sensorInfo);    //find text box for sensor info display
        textViewSensorInfo.setText(R.string.text_spiner_init);               //Set text when no item selected

        // Initialize and assign variable of the bottom navigation tab
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set About Page selected
        bottomNavigationView.setSelectedItemId(R.id.about);

        // Perform item selected listener for the bottom navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

        /** Function dealing with the bottom navigation bar actions
         * input: item of the navigation menu (on selected)
         * output: boolean saying if valid item selected or not **/
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch(item.getItemId())    //Switch views depending on the item selected
            {
                case R.id.dashboard:    //Go to dashboard view and activity
                    startActivity(new Intent(getApplicationContext(),DashBoard.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.about:    //Remain on this page and activity
                    return true;
                case R.id.home:     //Go to home page and main activity
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;   //Invalid button - should not be reachable
        }
    });
    }

    /** Function dealing with the dropdown menu actions
     * input: -
     * output: -
     * It sets the sensor information text box according to the menu position choosen**/

    private void initspinnerfooter() {

        //Initialise adapter for the menu
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorListDropDown);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /** Function dealing with dropdown menu on click
             * It sets the sensor information text box according to the menu position choosen based on
             * the hashmap sensorTypes**/
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK); // Set color of the dropdown menu text

                int currSensor = sensorTypes.get(parent.getItemAtPosition(position)); //get sensor type based on the hashmap and item selected
                SensorDetails currentDisplaySensor = sensorDetails(currSensor);       //get sensor details of the selected sensor from the SensorDetails object
                textViewSensorInfo.setText(Html.fromHtml(
                        "<b>Name: </b>"+ currentDisplaySensor.name
                        +"<br> <b> Vendor: </b> " + currentDisplaySensor.vendor
                        +"<br> <b> Version: </b>"+currentDisplaySensor.version
                        +"<br> <b>Power:</b>  "+ currentDisplaySensor.power
                        +"<br><b>Resolution: </b> "+ currentDisplaySensor.res));    //Set text view - use html syntaxt for bold headings
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textViewSensorInfo.setText(R.string.text_spiner_init);               //Set text when no item selected
            }
        });
    }
}
