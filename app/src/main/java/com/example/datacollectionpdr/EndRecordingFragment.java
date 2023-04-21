package com.example.datacollectionpdr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import java.util.Objects;

/**fragment that deals with setting user location and orientation after recording is paused
 * and then lets user discard or save the recording
 *
 *  * Author: Dagna Wojciak
 *  * Affiliation: The University of Edinburgh
 */
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
    private boolean pointsSet;  //boolean values indicating whether user provided points

    public EndRecordingFragment() {
        // Required empty public constructor
    }

    /** initialise activity*/
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
        sendButton = (Button) view.findViewById(R.id.button_send);
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
        long Tinit=(((RecordingActivity) requireActivity()).trajectoryNative.initTime);
        int pdrSize=((RecordingActivity) requireActivity()).trajectoryNative.getMotionSample().size();

        //Set text view to display recording time or a warning if no data was collected
        if (pdrSize != 0){
            long Tfinal=(((RecordingActivity) requireActivity()).trajectoryNative.getMotionSample().get(pdrSize- 1).initTime);
            recordingTime=String.valueOf(Tinit-Tfinal);
            timeTextView.setText(R.string.recordingDuration + recordingTime);
        }else {
            timeTextView.setText(R.string.recordingDuration_noData);
        }

        // Set up Async map
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(googleMap -> {

            if (((RecordingActivity) requireActivity()).curGNSSData != null) {  //Get current location from gps if available
                currLon = ((RecordingActivity) requireActivity()).curGNSSData.lon;
                currLat = ((RecordingActivity) requireActivity()).curGNSSData.lat;

                // move the camera to the current position
                LatLng currPos = new LatLng(currLat, currLon);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLon) , 19.0f));
            }

            //Set up goole map UI
            googleMap.getUiSettings().setCompassEnabled(true);          //Compass
            googleMap.getUiSettings().setRotateGesturesEnabled(true);   //rotation
            googleMap.getUiSettings().setScrollGesturesEnabled(true);   //Scrolling
            googleMap.getUiSettings().setTiltGesturesEnabled(true);     //Tilt

            //Warn user if permissions not granted
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "No permission to access location data!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                googleMap.setMyLocationEnabled(true);   //Set up the pointer button that lets user see current location
            }

            startMarker = googleMap.addMarker(  //Add (invisible for now) marker that will store user start location
                    new MarkerOptions()
                            .position(new LatLng(0,0))
                            .draggable(true).visible(false)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            //Set up google map listener that will detect users clicks on the map and make markers
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng point) {
                    if(!pointsSet) {  //Only place new markers if markes are not set yet

                        if (currMarker != null) { //Remove old marker when new one selected
                            currMarker.remove();
                        }
                        currMarker = googleMap.addMarker(   //When user placed a new marker
                                new MarkerOptions()
                                        .position(new LatLng(point.latitude, point.longitude))
                                        .title("Selected Location")
                                        .draggable(true).visible(true)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        RecordingActivity.currPointCoordinates[0] = point.latitude; //save coordinates
                        RecordingActivity.currPointCoordinates[1] = point.longitude;//save coordinates
                    }
                }
            });
        });
        return view;    //Update view
    }

    /**function dealing with buttons actions - its outcome depends on the button clicked*/
    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.button_discard:   //Discard button -> go back to main activity and discard the data
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent); //Go to the Show Help activity and its view
                ((Activity) requireActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.button_send:  //->Go to review page and apply trajectory corrections based on user points input
                UserPositionData endPos = new UserPositionData(RecordingActivity.endCoordinates[0], RecordingActivity.endCoordinates[1], RecordingActivity.endCoordinates[2], RecordingActivity.endCoordinates[3]);
                TrajectoryNative trajectoryNative = ((RecordingActivity) requireActivity()).trajectoryNative;
                trajectoryNative.applyGyroCorrection(endPos);   //correct gyro drift
                trajectoryNative.applyTrajectoryScaling(endPos);    //Correct scaling to the steps
                // trajectoryNative.applyAdditiveCorrection(endPos); (Possible alternative correction option)
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity, new ReviewFragment());
                fragmentTransaction.commit();   //go to review fragment
                break;

            case R.id.button_addEndPoint:   //end location button -> save its coordinates and change color
                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else {
                    //save coordinates to endCoordinates
                    RecordingActivity.endCoordinates[0] = RecordingActivity.currPointCoordinates[0];
                    RecordingActivity.endCoordinates[1] = RecordingActivity.currPointCoordinates[1];

                    //enable the orientation button
                    orientationButton.setEnabled(true);
                    orientationButton.setTextColor(getResources().getColor(R.color.black));

                    //disable location button
                    locationButton.setEnabled(false);
                    locationButton.setTextColor(getResources().getColor(R.color.blue_light));

                    //enable the start pos marker (orange)
                    startMarker.setPosition(currMarker.getPosition());
                    startMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    startMarker.setTitle("Start Position");
                    startMarker.setVisible(true);
                    currMarker.remove();
                    currMarker=null;
                }
                break;

            case R.id.button_addEndDirection:   //orientation button -> save orientation point coordinates and change marker and buttons apperance

                if (currMarker == null) { //Check if user put in a marker
                    Toast.makeText(getContext(), "Place a marker on the map first", Toast.LENGTH_SHORT).show();
                } else {
                    RecordingActivity.endCoordinates[2] = RecordingActivity.currPointCoordinates[0];
                    RecordingActivity.endCoordinates[3] = RecordingActivity.currPointCoordinates[1];

                    //Enable send button
                    sendButton.setEnabled(true);
                    sendButton.setTextColor(getResources().getColor(R.color.black));

                    //Change color and name of the map marker
                    currMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    startMarker.setTitle("Start Orientation");

                    //disable orientation button and setting new markers on the map
                    orientationButton.setTextColor(getResources().getColor(R.color.blue_light));
                    orientationButton.setEnabled(false);
                    pointsSet=true;
                }
                break;
        }
    }
}