package com.example.datacollectionpdr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidplot.xy.XYPlot;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.serializationandserver.FileManager;
import com.example.datacollectionpdr.serializationandserver.ServerManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Objects;


/**
 * Class for the activity where past saved files with trajectories can be viewed and managed
 */
public class DashBoard extends AppCompatActivity implements View.OnClickListener{

    String[] files;                      //string array with stored trajectories
    XYPlot plot;                         //plot for displaying the trajectories
    TrajectoryNative trajectoryNative;   //trajectory object
    Spinner dropdown;                    //dropdown menu
    String currentDisplayFile;           //string with the currently selected file

    Button sendButton;                  //Send Button
    Button loadButton;                  //Load Button
    Button deleteButton;                //Delete Button

    //TODO add displays for other information like data ID, duration, etc

    /** on create function to set up the view for activity and initialise all the elements*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        files = FileManager.seeFiles(getApplicationContext());  //create a list of fies found in the memory

        setContentView(R.layout.activity_dashboard);  //Set up view from activity_dashboard xml

        Objects.requireNonNull(getSupportActionBar()).hide();                 //Hide app bar

        currentDisplayFile = null;                   //initialise dropdown with nothing choosen
        dropdown = findViewById(R.id.spinner_files); // initialize drop down menu reference:
        initspinnerfooter();                         //set up the drop down menu

        sendButton = findViewById(R.id.button_sendfromfile);    //Initialise button for sending to server
        sendButton.setOnClickListener(this);                    //set button listener

        loadButton = findViewById(R.id.button_load);            //Initialise button for sending to server
        loadButton.setOnClickListener(this);                    //set button listener

        deleteButton = findViewById(R.id.button_delete);        //Initialise button for sending to server
        deleteButton.setOnClickListener(this);                  //set button listener

        plot = (XYPlot) findViewById(R.id.plot3);               //initialise plot for displaying trajectory

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation); //initialise bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.dashboard);     // Set Dashboard page selected on the bottom menu

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
        switch (v.getId()) {
            //Send currently loaded trajectory
            case R.id.button_sendfromfile:  //send button -> send selected file to server
                ServerManager.sendData(this.trajectoryNative, MainActivity.serverKeyString);
                break;

            //Load currently selected trajectory
            case R.id.button_load: //load button -> show trajectory saved in the chosen graph on the plot
                Log.e("button", "Starting load of " + currentDisplayFile);
                try{
                    this.trajectoryNative = FileManager.getTrajectoryFile(getApplicationContext(), currentDisplayFile);
                    UITools.plotPDRTrajectory(this.trajectoryNative.getPdrs(), Color.RED, plot);
                } catch (IOException e) {
                    Log.e("Load Failure", e.toString());
                }

            //delete currently selected trajectory
            case R.id.button_delete: //delete button -> delete the chosen file
                try{
                    FileManager.deleteFile(getApplicationContext(), currentDisplayFile);
                    updateAvailableFiles();
                } catch (IOException e) {
                    Log.e("File Delete Failure", currentDisplayFile + " Failed to Delete");
                }
        }
    }

    /**Recalculates what files are available and initializes spinner*/
    private void updateAvailableFiles(){
        files = FileManager.seeFiles(getApplicationContext());  //create a list of fies found in the memory
        initspinnerfooter();
    }


    /**set up back button to go to home page when its clicked*/
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));  //go to the home page (main activity)
        overridePendingTransition(0,0);
    }

    /**function that sets up the drop down menu allowing user to pick the sensor which data is displayed*/
    private void initspinnerfooter() {

        //get the layout element
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, files);
        dropdown.setAdapter(adapter);   //set up view adapter

        //when a new item is selected from the menu look for the position selected and put corresponding name to string
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));     //check which item chosen
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);   //display the selected item when drop down menu rolled up

                currentDisplayFile=(String) parent.getItemAtPosition(position);   //update the string indicating the chosen file
            }

            //set the default selected file to the first item on the list
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentDisplayFile = files[0];
            }
        });
    }

}
