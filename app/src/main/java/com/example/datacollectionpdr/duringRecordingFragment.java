package com.example.datacollectionpdr;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_during_recording, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // find views by id
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager11);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        Button sendButton = (Button) view.findViewById(R.id.button_endRecording);

        sendButton.setOnClickListener(this);
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        // add the fragments
        viewPagerAdapter.add(new MapFragment(), "MAP");
        viewPagerAdapter.add(new GraphsFragment(), "SENSOR DATA");
        viewPagerAdapter.add(new PathFragment(), "TRAJECTORY");

        // set adapter on viewpager
        viewPager.setAdapter(adapter);
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