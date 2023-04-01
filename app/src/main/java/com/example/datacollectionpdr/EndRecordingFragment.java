package com.example.datacollectionpdr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;


public class EndRecordingFragment extends Fragment implements View.OnClickListener{

    View view;
    private double currLon;
    private  double currLat;

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
        Button sendButton = (Button) view.findViewById(R.id.button_send);
        sendButton.setOnClickListener(this);
        Button server_apiButton = (Button) view.findViewById(R.id.enter_server_api);
        server_apiButton.setOnClickListener(this);
        Button endPosButton = (Button) view.findViewById(R.id.button_addEndPoint);
        endPosButton.setOnClickListener(this);
        Button endPosFacing = (Button) view.findViewById(R.id.button_addEndDirection);
        endPosFacing.setOnClickListener(this);
        TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);
        textInputLayout.setHint("Current ID: "+ MainActivity.serverKeyString);

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

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("New Marker");
                        googleMap.addMarker(marker);
                        Log.d("point", "onMapClick() returned: " + point.latitude);
                        Log.d("point", "onMapClick() returned: " + point.longitude);
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
            case R.id.button_send:
                Intent intent_send = new Intent(getActivity(), MainActivity.class);
                startActivity(intent_send); //Go to the Show Help activity and its view
                ((Activity) getActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.enter_server_api:
                TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);
                String text = textInputLayout.getEditText().getText().toString();
                MainActivity.serverKeyString=text;
                textInputLayout.setHint("Current ID: "+ MainActivity.serverKeyString);
                break;
            case R.id.button_addEndPoint:
                RecordingActivity.endCoordinates[0] = RecordingActivity.currPointCoordinates[0];
                RecordingActivity.endCoordinates[1] = RecordingActivity.currPointCoordinates[1];
                break;
            case R.id.button_addEndDirection:
                RecordingActivity.endCoordinates[2] = RecordingActivity.currPointCoordinates[0];
                RecordingActivity.endCoordinates[3] = RecordingActivity.currPointCoordinates[1];
                break;
        }
    }
}