package com.example.datacollectionpdr;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Fragment class that shown google map to the user while recording with indication of the phone position
 * according to GPS readings
 */
public class MapFragment extends Fragment {

    private double currLon;     //current longitude from gps
    private  double currLat;    //current latitude from gps

    /**
     * Set up of the view
     * @param inflater - puts layout from fragment map xml file into the fragment view
     * @param container - the activity view in which map is placed
     * @param savedInstanceState - ragment saved state
     * @return - created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                if (((RecordingActivity) requireActivity()).curGNSSData != null) {  //if access to GPS iven and location available

                    //Get current location
                    currLon = ((RecordingActivity) requireActivity()).curGNSSData.lon;
                    currLat = ((RecordingActivity) requireActivity()).curGNSSData.lat;

                    // move the camera to the current position
                    LatLng currPos = new LatLng(currLat, currLon);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currPos));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLon) , 14.0f));
                }
                //Set up goole map UI
                googleMap.getUiSettings().setCompassEnabled(true);          //Compass
                googleMap.getUiSettings().setRotateGesturesEnabled(true);   //rotation
                googleMap.getUiSettings().setScrollGesturesEnabled(true);   //Scrolling
                googleMap.getUiSettings().setTiltGesturesEnabled(true);     //Tilt

                //enable to automatically show user location if all permissions granted
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            }
        });
        return view;
    }
}
