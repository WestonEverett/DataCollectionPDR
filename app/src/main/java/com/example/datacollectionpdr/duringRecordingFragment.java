package com.example.datacollectionpdr;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.datacollectionpdr.nativedata.MotionSample;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.tabs.TabLayout;


public class duringRecordingFragment extends Fragment implements View.OnClickListener {

    View view;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public duringRecordingFragment() {
        // Required empty public constructor
    }


    public static duringRecordingFragment newInstance() {
        duringRecordingFragment fragment = new duringRecordingFragment();


        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // setting up the adapter
        viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());

        // add the fragments
        viewPagerAdapter.add(new MapFragment(), "MAP");
        viewPagerAdapter.add(new GraphsFragment(), "SENSOR DATA");
        viewPagerAdapter.add(new PathFragment(), "TRAJECTORY");

        // Set the adapter
        viewPager.setAdapter(viewPagerAdapter);

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout);

        view = inflater.inflate(R.layout.fragment_during_recording, container, false);
        Button sendButton = (Button) view.findViewById(R.id.button_endRecording);
        sendButton.setOnClickListener(this);

        return inflater.inflate(R.layout.fragment_during_recording, container, false);
    }


    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity, new EndRecordingFragment());
        fragmentTransaction.commit();
    }
}