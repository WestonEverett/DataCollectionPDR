package com.example.datacollectionpdr;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.datacollectionpdr.R;
import com.example.datacollectionpdr.nativedata.PDRStep;

import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class PathFragment extends Fragment {

    private DataViewModel viewModel;
    XYPlot plot;
    private int curFloor = 0;
    private ArrayList<PDRStep> curSteps = new ArrayList<>();

    public PathFragment() {
        // required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_path, container, false);
        // initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.plot);

        RecordingActivity activity = (RecordingActivity) getActivity();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        viewModel.getPDRStep().observe(getViewLifecycleOwner(), item -> {

            if(item.estFloor != curFloor){
                curSteps = new ArrayList<>();
                curFloor = item.estFloor;
            }

            curSteps.add(item);
            plotPDRTrajectory(curSteps);
            //TODO GRAPH
        });
    }


    public void plotPDRTrajectory(ArrayList<PDRStep> steps){

        ArrayList<Number> xVals = new ArrayList<>();
        ArrayList<Number> yVals = new ArrayList<>();

        float curX = 0;
        float curY = 0;

        xVals.add(curX);
        yVals.add(curY);

        for(PDRStep step : steps){
            curX = curX + step.x;
            curY = curY + step.y;

            xVals.add(curX);
            yVals.add(curY);
        }


        //Log.d("plt", "Value: " + Float.toString((Float) series1Numbers[0]));
        //Log.d("plt", "Current Display Sensor: " + currentDisplaySensor);
        //Log.d("plt","should be showing a value");

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        SimpleXYSeries series = new SimpleXYSeries(xVals, yVals, "PDR Steps on Floor: " + String.valueOf(steps.get(0).estFloor));

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.RED, null, null);
        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);
        LineAndPointFormatter series3Format = new LineAndPointFormatter(Color.GREEN, Color.GREEN, null, null);


        if (plot !=null){
            plot.clear();


            plot.addSeries(series, new LineAndPointFormatter(Color.BLUE, null, null, null));

            // add a new series' to the xyplot:
            plot.addSeries(series, series1Format);

            plot.redraw();
        }
    }
}
