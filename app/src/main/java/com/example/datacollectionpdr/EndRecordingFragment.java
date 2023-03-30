package com.example.datacollectionpdr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
        TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);
        textInputLayout.setHint("Current ID: "+ MainActivity.serverKeyString);

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                // Add a marker in Sydney and move the camera
                LatLng currPos = new LatLng(-3.188267, -55.953251);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));

                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.getUiSettings().setTiltGesturesEnabled(true);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("New Marker");
                        googleMap.addMarker(marker);
                        //RecordingActivity.currPosCoordinates[1] = point.latitude;
                        //RecordingActivity.currPosCoordinates[2] = point.longitude;
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
        }
    }
}