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

import java.util.Locale;

public class DuringRecordingFragment extends Fragment implements View.OnClickListener {

    View view;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager adapter;
    private TabLayout tabLayout;
    public TextView timeView;
    public int seconds = 0;
    public boolean running; // Is the stopwatch running?

    public DuringRecordingFragment() {
        // Required empty public constructor
    }

    public static DuringRecordingFragment newInstance() {
        DuringRecordingFragment fragment = new DuringRecordingFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting up the adapter

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_during_recording, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views by id
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager11);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        Button endRecordingButton = (Button) view.findViewById(R.id.button_endRecording);

        endRecordingButton.setOnClickListener(this);

        // attach tablayout with viewpager
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

// add the fragments
        adapter.add(new MapFragment(), "MAP");
        adapter.add(new GraphsFragment(), "SENSOR DATA");
        adapter.add(new PathFragment(), "TRAJECTORY");

        // set adapter on viewpager
        viewPager.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity, new EndRecordingFragment());
        ((RecordingActivity) getActivity()).stopRecording();
        fragmentTransaction.commit();
    }


    // Sets the NUmber of seconds on the timer.
    // The runTimer() method uses a Handler to increment the seconds and update the text view.
    private void runTimer(View view) {
        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method, passing in a new Runnable. The post() method processes
        // code without a delay, so the code in the Runnable will run almost immediately.
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,and seconds.
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);

                if(view != null) { // Set the text view text.
                    TextView timeView = (TextView) view.findViewById(R.id.time_view);
                    timeView.setText(time);
                    Log.d("timer","textwiev not null");
                }
                else{
                    Log.d("timer","textwiev NULL_1");
                }
                // If running is true, increment the seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }
}