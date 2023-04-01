package com.example.datacollectionpdr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;


public class StartRecFragment extends Fragment implements View.OnClickListener {

    View view;                  //View of the fragment
    Button sendButton;          //Send Button
    Button locationButton;      //Button to set Users start Location
    Button orientationButton;   //Button to set users start orientation
    Marker currMarker;          //Marker showing the latest click on the map
    Marker startMarker;         //Marker showing selected position

    private double currLon;
    private double currLat;

    public StartRecFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) { //activity created
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_start_rec, container, false); // Inflate the layout for this fragment

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {         //Display Google Map

                if (((RecordingActivity) getActivity()).curGNSSData != null) {
                    //Get current location
                    currLon = ((RecordingActivity) getActivity()).curGNSSData.lon;
                    currLat = ((RecordingActivity) getActivity()).curGNSSData.lat;
                    // move the camera to the current position
                    LatLng currPos = new LatLng(currLat, currLon);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLon) , 14.0f));
                }

                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setTiltGesturesEnabled(true);

                startMarker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(0,0))
                                .title("New Marker")
                                .draggable(true).visible(false)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                else {
                    googleMap.setMyLocationEnabled(true);
                }

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {

                            if (currMarker != null) { //Remove old marker when new one selected
                                currMarker.remove();
                            }

                        currMarker = googleMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(point.latitude, point.longitude))
                                    .title("New Marker")
                                    .draggable(true).visible(true)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        RecordingActivity.currPointCoordinates[0] = point.latitude;
                        RecordingActivity.currPointCoordinates[1] = point.longitude;
                    }
                });
            }
        });

        // Inflate the layout for this fragment
        return  view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        sendButton = (Button) view.findViewById(R.id.button_startRec);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);

        locationButton = (Button) view.findViewById(R.id.button_addStartPoint);
        locationButton.setOnClickListener(this);

        orientationButton = (Button) view.findViewById(R.id.button_addStartDirection);
        orientationButton.setOnClickListener(this);
        orientationButton.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.button_startRec:
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity, new DuringRecordingFragment());
                ((RecordingActivity) getActivity()).startRecording();
                fragmentTransaction.commit();
                break;
            case R.id.button_addStartPoint:

                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else{
                    RecordingActivity.startCoordinates[0] = RecordingActivity.currPointCoordinates[0];
                RecordingActivity.startCoordinates[1] = RecordingActivity.currPointCoordinates[1];
                orientationButton.setEnabled(true);
                locationButton.setEnabled(false);
                startMarker.setPosition(currMarker.getPosition());
                startMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                startMarker.setVisible(true);
                currMarker.remove();
                 }

                break;
            case R.id.button_addStartDirection:

                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else {
                    RecordingActivity.startCoordinates[2] = RecordingActivity.currPointCoordinates[0];
                    RecordingActivity.startCoordinates[3] = RecordingActivity.currPointCoordinates[1];
                    sendButton.setEnabled(true);
                    currMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                break;

        }
    }
}