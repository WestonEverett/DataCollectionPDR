package com.example.datacollectionpdr;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import java.util.Locale;

/** Class defining the main actions in the during recording activity - the swiped tab layout to see
the sensor and pdr info + display a recording timer and stop button
 * Author: Dagna Wojciak
 * Affiliation: The University of Edinburgh
 */
public class DuringRecordingFragment extends Fragment implements View.OnClickListener {

    View fragmentView;                  //fragment view
    public ViewPagerAdapter adapter;    //adapter for the tab view
    public ViewPager viewPager;         //adapter for the tab viw
    public TabLayout tabLayout;         //initialise the scrolling tab view
    public Button endRecordingButton;   //Initialise button
    public int seconds = 0;             //initialise stopwatch
    public boolean running;             //Is the stopwatch running?

    public DuringRecordingFragment() {
        // Required empty public constructor
    }

    //Initialise fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**Initialise the fragment view - from fragment_during_recording layout file*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_during_recording, container, false);
        return fragmentView;
    }

    /**
     When view for the fragment is created initialise all the views, buttons etc.
     input: saved state of the activity
     output: view from fragment review layout with all the buttons,text inputs etc.*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views by id
        viewPager = (ViewPager) view.findViewById(R.id.viewpager11);    //swiped page menu
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);     //Tabs for the menu
        endRecordingButton = (Button) view.findViewById(R.id.button_endRecording);  //Button - stop

        endRecordingButton.setOnClickListener(this);                //set listener fo rthe button
        tabLayout.setupWithViewPager(viewPager);                    // attach tablayout with viewpager
        adapter = new ViewPagerAdapter(getChildFragmentManager());  //set up the pages in tab layout

        // add the fragments to adapter
        adapter.add(new MapFragment(), "MAP");
        adapter.add(new GraphsFragment(), "SENSOR DATA");
        adapter.add(new PathFragment(), "TRAJECTORY");

        viewPager.setAdapter(adapter); // set adapter on viewpager

        runTimer(view);     //Set up timer widget
        running= true;      //Start Timing
    }

    /** what you want to do when stop recording button is clicked*/
    @Override
    public void onClick(View v) {

        running= false; //Stop timer

        //Change fragment to end recording fragment
        FragmentTransaction fragmentTransaction = requireActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity,
                new EndRecordingFragment());

        ((RecordingActivity) requireActivity()).stopRecording();    //Stop collecting data
        fragmentTransaction.commit();
    }


    /** Sets the NUmber of seconds on the timer. The runTimer() method uses a Handler to
     * increment the seconds and update the text view using interrupts */
    private void runTimer(View view) {

        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method, passing in a new Runnable. The post() method processes
        // code without a delay, so the code in the Runnable will run almost immediately.
        handler.post(new Runnable() {
            @Override
            public void run() {     //Method dealing with the running timer

                //Convert time in seconds to minutes and hours format
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,and seconds string
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);

                if(view != null) { // Set the text view text.
                    TextView timeView = (TextView) view.findViewById(R.id.time_view_bottom);
                    timeView.setText(time);
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