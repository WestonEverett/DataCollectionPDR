package com.example.datacollectionpdr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;
import com.example.datacollectionpdr.data.Traj;
import com.example.datacollectionpdr.nativedata.PDRStep;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.UserPositionData;
import com.example.datacollectionpdr.serializationandserver.FileManager;
import com.example.datacollectionpdr.serializationandserver.ServerManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class DashBoard extends AppCompatActivity implements View.OnClickListener{

    String[] files;
    String selectedFile;
    XYPlot plot;
    TrajectoryNative trajectoryNative;
    Spinner dropdown;                               //initialise dropdown menu
    String currentDisplayFile;                    //initialise string with the currently selected sensor

    //TODO hook up to UI, add displays for other information like data ID, duration, etc
    /** on create function to set up the view for activity and initialise all the elements*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        files = FileManager.seeFiles(getApplicationContext());

        setContentView(R.layout.activity_dashboard);    //Set up view from activity_dashboard xml

        getSupportActionBar().hide();   //Hide app bar

        currentDisplayFile = null;
        dropdown = findViewById(R.id.spinner_files);// initialize drop down menureference:
        initspinnerfooter();                        //set up the menu


        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Dashboard selected
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        // Perform Bottom navigation item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.dashboard:
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0,0);
                        return true;
                } return false;
            }
        });
    }


    /**function dealing with buttons actions - its outcome depends on the button clicked*/
    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.button_sendfromfile:
                ServerManager.sendData(this.trajectoryNative, MainActivity.serverKeyString);
                break;

            case R.id.button_load:
                try{
                    this.trajectoryNative = FileManager.getTrajectoryFile(getApplicationContext(), selectedFile);
                    UITools.plotPDRTrajectory(this.trajectoryNative.getPdrs(), Color.RED, plot);
                } catch (IOException e) {
                    Log.e("Load Failure", selectedFile + " Failed to Load");
                }

            case R.id.button_delete:
                //try{
                    //TODO delete the file
                ///} catch (IOException e) {
                   // Log.e("File Delete Failure", selectedFile + " Failed to Delete");
                //}
        }
    }


    //TODO to get stuff from server

    //TODO list display for files



    /**set up back button to go to home page*/
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        overridePendingTransition(0,0);
    }

    /**function that sets up the drop down menu allowing user to pick the sensor which data is displayed
     */
    private void initspinnerfooter() {

        //get the layout element
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, files);
        dropdown.setAdapter(adapter);   //set up view adater

        //when a new item is selected from the menu
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));     //check which item choosen
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);   //display the selected item when drop down menu rolled up

                currentDisplayFile=(String) parent.getItemAtPosition(position);   //update the string indicating the choosen sensor
            }

            //set the default seected sensor
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentDisplayFile = files[1];
            }
        });
    }

}
