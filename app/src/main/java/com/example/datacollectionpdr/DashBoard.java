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

    //TODO hook up to UI, add displays for other information like data ID, duration, etc
    /** on create function to set up the view for activity and initialise all the elements*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        files = FileManager.seeFiles(getApplicationContext());
        setContentView(R.layout.activity_dashboard);    //Set up view from activity_dashboard xml

        getSupportActionBar().hide();   //Hide app bar

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
            case 1: //R.id.button_send:  //->Go to review page and apply trajectory corrections based on user points input
                ServerManager.sendData(this.trajectoryNative, MainActivity.serverKeyString);
                break;

            case 2: //R.id.button_load:
                try{
                    this.trajectoryNative = FileManager.getTrajectoryFile(getApplicationContext(), selectedFile);
                    UITools.plotPDRTrajectory(this.trajectoryNative.getPdrs(), Color.RED, plot);
                } catch (IOException e) {
                    Log.e("Load Failure", selectedFile + " Failed to Load");
                }
        }
    }

    //TODO Button to update files list

    //TODO to delete all files

    //TODO to get stuff from server

    //TODO list display for files

    //TODO Ideally add delete and show buttons - spearate fragment

    /**set up back button to go to home page*/
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        overridePendingTransition(0,0);
    }



}
