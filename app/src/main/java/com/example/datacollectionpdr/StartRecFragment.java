package com.example.datacollectionpdr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.datacollectionpdr.nativedata.UserPositionData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

/**
 * Class managing the start recording fragment where user chooses start location
 * Author:  Dagna Wojciak
 * Affiliation: The University of Edinburgh
 */

public class StartRecFragment extends Fragment implements View.OnClickListener {

    View view;                  //View of the fragment
    Button sendButton;          //Send Button
    Button locationButton;      //Button to set Users start Location
    Button orientationButton;   //Button to set users start orientation
    Marker currMarker;          //Marker showing the latest click on the map
    Marker startMarker;         //Marker showing selected position

    private double currLon;    //longitude of the currently selected marker
    private double currLat;    //latitude of the currently selected marker
    private boolean pointsSet;  //Is the start location and orientation selected?

    public StartRecFragment() {
        // Required empty public constructor
    }

    /**
     * Initialise variables when fragment created
     * @param savedInstanceState - previous state of the fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) { //activity created
        super.onCreate(savedInstanceState);
        pointsSet=false;        //Initialise saying that points arent chosen
    }

    /**
     * Actions to be done when fragment view is created - initialise UI
     * @param inflater - fragment set up
     * @param container - fragment view container in activity layout
     * @param savedInstanceState - previous state of the fragment
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_start_rec, container, false); // Inflate the layout for this fragment

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {         //Display Google Map

                if (((RecordingActivity) requireActivity()).curGNSSData != null) {

                    //Get current location
                    currLon = ((RecordingActivity) requireActivity()).curGNSSData.lon;
                    currLat = ((RecordingActivity) requireActivity()).curGNSSData.lat;

                    // move the camera to the current position
                    LatLng currPos = new LatLng(currLat, currLon);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLon) , 14.0f));
                }

                googleMap.getUiSettings().setCompassEnabled(true);          //compass
                googleMap.getUiSettings().setRotateGesturesEnabled(true);   //rotation
                googleMap.getUiSettings().setScrollGesturesEnabled(true);   //scroll
                googleMap.getUiSettings().setTiltGesturesEnabled(true);     //tilt

                //initialise marker for the start point (invisible until location choosen)
                startMarker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(0,0))  //non-important coordinates
                                .title(" ")                                 //empty label
                                .draggable(true).visible(false));           //not visible

                //check location permision for the map and set map location if permission granted
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }

                //set up click listener for the map so that user can place markers
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        if(!pointsSet) {  //Do stuff only if location and orientation not already selected

                            if (currMarker != null) { //Remove old marker when new one selected
                                currMarker.remove();
                            }

                            currMarker = googleMap.addMarker(   //add marker at the location chosen by user
                                    new MarkerOptions()
                                            .position(new LatLng(point.latitude, point.longitude))  //set point coordinates
                                            .title("Selected Position")
                                            .draggable(true).visible(true)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            RecordingActivity.currPointCoordinates[0] = point.latitude; //save selected latitude into a temporary array
                            RecordingActivity.currPointCoordinates[1] = point.longitude;//save selected longitude into a temporary array
                        }
                    }
                });
            }
        });

        // Inflate the layout for this fragment
        return  view;
    }

    /**
     * Set up listeners when view is ready
     * @param view - fragment view
     * @param savedInstanceState - saved instance of fragment state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Send button - dis enabled until location and orientation selected
        sendButton = (Button) view.findViewById(R.id.button_startRec);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);
        sendButton.setTextColor(getResources().getColor(R.color.blue_light));

        //Location button - choose starting point
        locationButton = (Button) view.findViewById(R.id.button_addStartPoint);
        locationButton.setOnClickListener(this);

        //orientation button - select orientation - enabled only after location selected
        orientationButton = (Button) view.findViewById(R.id.button_addStartDirection);
        orientationButton.setOnClickListener(this);
        orientationButton.setEnabled(false);
        orientationButton.setTextColor(getResources().getColor(R.color.blue_light));
    }

    /**
     *Do what you want to do when one of the buttons is clicked
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_startRec:  //start recording button - > go to recording button and restart saving data
                FragmentTransaction fragmentTransaction = requireActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity, new DuringRecordingFragment());
                ((RecordingActivity) requireActivity()).startRecording(new UserPositionData(RecordingActivity.startCoordinates[0], RecordingActivity.startCoordinates[1], RecordingActivity.startCoordinates[2], RecordingActivity.startCoordinates[3]));
                fragmentTransaction.commit();
                break;

            case R.id.button_addStartPoint: //add start point -> take the placed marker and save as a start point

                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else{     //when marker is placed as it should
                    //save point coordinates
                    RecordingActivity.startCoordinates[0] = RecordingActivity.currPointCoordinates[0];
                    RecordingActivity.startCoordinates[1] = RecordingActivity.currPointCoordinates[1];
                    //enable orientation button
                    orientationButton.setEnabled(true);
                    orientationButton.setTextColor(getResources().getColor(R.color.black));
                    //dis enable location button (it is already selected)
                    locationButton.setEnabled(false);
                    locationButton.setTextColor(getResources().getColor(R.color.blue_light));
                    sendButton.setTextColor(Color.parseColor("#5C6168"));
                    //add start marker to the map (use the marker initialised in on create and make it visible)
                    startMarker.setPosition(currMarker.getPosition());
                    startMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    startMarker.setTitle("Start Position");
                    startMarker.setVisible(true);
                    //Remove old marker when new one selected
                    if (currMarker != null) {
                        currMarker.remove();
                    }currMarker=null;
                 }break;

            case R.id.button_addStartDirection: //add start orientation point -> take the placed marker and save as a start point orientation

                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else {//when marker is placed as it should
                    //save point coordinates in the indexes 2 and 3 od the start coordinates array
                    RecordingActivity.startCoordinates[2] = RecordingActivity.currPointCoordinates[0];
                    RecordingActivity.startCoordinates[3] = RecordingActivity.currPointCoordinates[1];
                    //enable send button as now all information is there
                    sendButton.setEnabled(true);
                    sendButton.setTextColor(getResources().getColor(R.color.black));
                    //dis-enable orientation button
                    orientationButton.setEnabled(false);
                    orientationButton.setTextColor(getResources().getColor(R.color.blue_light));
                    //change marker appearance
                    currMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    //don't allow t place more markers on the map
                    pointsSet=true;
                }
                break;

        }
    }
}