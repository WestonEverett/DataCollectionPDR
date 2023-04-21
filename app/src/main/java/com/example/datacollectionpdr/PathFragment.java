package com.example.datacollectionpdr;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.androidplot.xy.XYPlot;
import com.example.datacollectionpdr.nativedata.PDRStep;
import java.util.ArrayList;

/**
 * Class with fragment that shows user PDR live when recording
 *  * Author: Dagna Wojciak, Weston Everett
 *  * Affiliation: The University of Edinburgh
 */
public class PathFragment extends Fragment {

    XYPlot plot;                                                //initialise XYplot plot
    private int curFloor = 0;                                   //initialise current floor level to 0
    private ArrayList<PDRStep> curSteps = new ArrayList<>();    //initialise step count
    TextView graphTitleText;
    String title = "PDR Trajectory on floor: ";

    public PathFragment() {
        // required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { //initialise fragment from saved previous state
        super.onCreate(savedInstanceState);
    }

    /**
     * function called to create the view - set up the plot window
     * @param inflater - to put fragment in the activity view
     * @param container - view in which fragment is nested
     * @param savedInstanceState - saved state of the fragment
     * @return view of the fragment
     */
    @Nullable  @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_path, container, false);//put fragment in a view container
        plot = (XYPlot) view.findViewById(R.id.plot);            // initialize  XYPlot reference:

        return view;
    }

    /**
     * When view is created set up the graph and initial plot
     * @param view - view initialised in onViewCreate
     * @param savedInstanceState - previous state of the fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set up acces to data passed from DataViewModel class
        DataViewModel viewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        viewModel.getPDRStep().observe(getViewLifecycleOwner(), item -> {   //set up listener to get updated PDR values from dataViewModel

            String titleFull=title + item.getEstFloor();
            graphTitleText = view.findViewById(R.id.textViewTitle);    //find text box for sensor info display
            graphTitleText.setText(titleFull);               //Set text when no item selected



            if(item.getEstFloor() != curFloor){
                curSteps = new ArrayList<>();
                curFloor = item.getEstFloor();
            }

            Toast.makeText(getContext(), String.valueOf(item.getEstFloor()), Toast.LENGTH_SHORT);

            curSteps.add(item);         //add another step to the trajectory
            UITools.plotPDRTrajectory(curSteps, Color.RED, plot);   //re-plot the plot
        });
    }
}
