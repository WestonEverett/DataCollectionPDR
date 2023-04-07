package com.example.datacollectionpdr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.example.datacollectionpdr.nativedata.UserPositionData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EndRecordingFragment extends Fragment implements View.OnClickListener{

    View view;
    Marker currMarker;          //Marker showing the latest click on the map
    Marker startMarker;         //Marker showing selected position
    Button sendButton;          //Send Button
    Button discardButton;       //Discard Button
    Button locationButton;      //Button to set Users start Location
    Button orientationButton;   //Button to set users start orientation
    double currLon;             //longitude from gps
    double currLat;             //latitude from gps
    String recordingTime;       //String fo displaying the timer
    TextView timeTextView;      //text view for the time display
    private boolean pointsSet;

    public EndRecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointsSet=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_end_recording, container, false); //Set up the fragment view

        //View elements set up
        timeTextView = (TextView) view.findViewById(R.id.textViewTime);        //Time text view

        // Button to review recording
        sendButton = (Button) view.findViewById(R.id.button_review);
        sendButton.setOnClickListener(this);  //
        sendButton.setEnabled(false);                                         //Dis-enable for now
        sendButton.setTextColor(getResources().getColor(R.color.blue_light)); //Change dis-enabled style

        //Button - add end point
        locationButton = (Button) view.findViewById(R.id.button_addEndPoint);
        locationButton.setOnClickListener(this);

        //Button Discard Recording
        discardButton = (Button) view.findViewById(R.id.button_discard);
        discardButton.setOnClickListener(this);

        //Button - add end direction
        orientationButton = (Button) view.findViewById(R.id.button_addEndDirection);
        orientationButton.setOnClickListener(this);
        orientationButton.setEnabled(false);                                         //Dis-enable for now
        orientationButton.setTextColor(getResources().getColor(R.color.blue_light)); //Change dis-enabled style

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
        getChildFragmentManager().findFragmentById(R.id.google_map);

        //Getting and displaying the recording duration based on the recorded data
        long Tinit=(((RecordingActivity)getActivity()).trajectoryNative.initTime);
        int pdrSize=((RecordingActivity)getActivity()).trajectoryNative.getMotionSample().size();

        //Set text view to display recording time or a warning if no data was collected
        if (pdrSize != 0){
            long Tfinal=(((RecordingActivity)getActivity()).trajectoryNative.getMotionSample().get(pdrSize- 1).initTime);
            recordingTime=String.valueOf(Tinit-Tfinal);
            timeTextView.setText(R.string.recordingDuration + recordingTime);
        }else {
            timeTextView.setText(R.string.recordingDuration_noData);
        }


        // Set up Async map
        supportMapFragment.getMapAsync(googleMap -> {

            if (((RecordingActivity) getActivity()).curGNSSData != null) {
                //Get current location
                currLon = ((RecordingActivity) getActivity()).curGNSSData.lon;
                currLat = ((RecordingActivity) getActivity()).curGNSSData.lat;
                // move the camera to the current position
                LatLng currPos = new LatLng(currLat, currLon);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLon) , 19.0f));
            }


            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "No permission to access location data!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                googleMap.setMyLocationEnabled(true);
            }

            startMarker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(0,0))
                            .draggable(true).visible(false)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    if(pointsSet==false) {

                        if (currMarker != null) { //Remove old marker when new one selected
                            currMarker.remove();
                        }
                        currMarker = googleMap.addMarker(
                                new MarkerOptions()
                                        .position(new LatLng(point.latitude, point.longitude))
                                        .title("Selected Location")
                                        .draggable(true).visible(true)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        RecordingActivity.currPointCoordinates[0] = point.latitude;
                        RecordingActivity.currPointCoordinates[1] = point.longitude;
                    }
                }
            });
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
                UserPositionData endPos = new UserPositionData(RecordingActivity.endCoordinates[0], RecordingActivity.endCoordinates[1], RecordingActivity.endCoordinates[2], RecordingActivity.endCoordinates[3]);
                TrajectoryNative trajectoryNative = ((RecordingActivity) getActivity()).trajectoryNative;
                trajectoryNative.applyGyroCorrection(endPos);
                trajectoryNative.applyTrajectoryScaling(endPos);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
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
                    startMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    startMarker.setTitle("Start Position");
                    startMarker.setVisible(true);
                    currMarker.remove();
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
                    currMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    startMarker.setTitle("Start Orientation");
                    orientationButton.setTextColor(getResources().getColor(R.color.blue_light));
                    orientationButton.setEnabled(false);
                    pointsSet=true;
                }
                break;
        }
    }
}