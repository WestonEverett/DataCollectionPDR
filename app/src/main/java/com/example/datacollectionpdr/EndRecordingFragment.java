package com.example.datacollectionpdr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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
import com.google.android.material.textfield.TextInputLayout;


public class EndRecordingFragment extends Fragment implements View.OnClickListener{

    View view;
    Marker currMarker;          //Marker showing the latest click on the map
    Marker startMarker;         //Marker showing selected position
    Button sendButton;          //Send Button
    Button discardButton;          //Send Button
    Button locationButton;      //Button to set Users start Location
    Button orientationButton;   //Button to set users start orientation
    double currLon;             //longitude from gps
    double currLat;             //lattitude from gps
    String recordingTime;

    public EndRecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_end_recording, container, false);
        sendButton = (Button) view.findViewById(R.id.button_review);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);
        sendButton.setTextColor(getResources().getColor(R.color.blue_light));

        locationButton = (Button) view.findViewById(R.id.button_addEndPoint);
        locationButton.setOnClickListener(this);

        discardButton = (Button) view.findViewById(R.id.button_discard);
        discardButton.setOnClickListener(this);

        orientationButton = (Button) view.findViewById(R.id.button_addEndDirection);
        orientationButton.setOnClickListener(this);
        orientationButton.setEnabled(false);
        orientationButton.setTextColor(getResources().getColor(R.color.blue_light));

        recordingTime=((RecordingActivity)getActivity()).trajectoryNative


        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                if (((RecordingActivity) getActivity()).curGNSSData != null) {
                    //Get current location
                    currLon = ((RecordingActivity) getActivity()).curGNSSData.lon;
                    currLat = ((RecordingActivity) getActivity()).curGNSSData.lat;
                    // move the camera to the current position
                    LatLng currPos = new LatLng(currLat, currLon);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLon) , 14.0f));
                }


                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setTiltGesturesEnabled(true);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                else {
                    googleMap.setMyLocationEnabled(true);
                }

                startMarker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(0,0))
                                .title("New Marker")
                                .draggable(true).visible(false)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


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
        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.button_discard:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent); //Go to the Show Help activity and its view
                ((Activity) getActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.button_review:
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity, new ReviewFragment());
                fragmentTransaction.commit();
                break;
            case R.id.button_addEndPoint:
                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else {
                    RecordingActivity.endCoordinates[0] = RecordingActivity.currPointCoordinates[0];
                    RecordingActivity.endCoordinates[1] = RecordingActivity.currPointCoordinates[1];
                    orientationButton.setEnabled(true);
                    orientationButton.setTextColor(getResources().getColor(R.color.black));
                    locationButton.setEnabled(false);
                    locationButton.setTextColor(getResources().getColor(R.color.blue_light));
                    startMarker.setPosition(currMarker.getPosition());
                    startMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    startMarker.setVisible(true);
                    currMarker=null;
                }
                break;
            case R.id.button_addEndDirection:

                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else {
                    RecordingActivity.endCoordinates[2] = RecordingActivity.currPointCoordinates[0];
                    RecordingActivity.endCoordinates[3] = RecordingActivity.currPointCoordinates[1];
                    sendButton.setEnabled(true);
                    sendButton.setTextColor(getResources().getColor(R.color.black));
                    currMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    orientationButton.setTextColor(getResources().getColor(R.color.blue_light));
                    orientationButton.setEnabled(false);
                }
                break;
        }
    }
}