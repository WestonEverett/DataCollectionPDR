package com.example.datacollectionpdr;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.graphics.*;

import android.widget.AdapterView;
import android.view.View;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;

public class GraphsFragment extends Fragment {

    String[] SensorList = { "Accelerometer", "Gyroscope",
            "Rotation"};

    public GraphsFragment() {
        // required empty public constructor.
    }
    XYPlot plot;
    Spinner dropdown;
    String currentDisplaySensor;
    private DataViewModel viewModel;
    private RecentMeasurements recentMeasurements;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_graphs, container, false);
        // initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.plot);

        RecordingActivity activity = (RecordingActivity) getActivity();

        dropdown = view.findViewById(R.id.spinner2);
        initspinnerfooter();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        viewModel.getSample().observe(getViewLifecycleOwner(), item -> {
            recentMeasurements.updateMeasurements(item);
            PlotThePlot(recentMeasurements.getData(currentDisplaySensor));
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recentMeasurements = new RecentMeasurements(200);
        currentDisplaySensor = "Accelerometer";

        PixelUtils.init(getActivity());
    }

    public void PlotThePlot(Number[][] PlotData){

        Number[] domainLabels=PlotData[0];
        Number[] series1Numbers=PlotData[1];
        Number[] series2Numbers=PlotData[2];
        Number[] series3Numbers=PlotData[3];

        //Log.d("plt", "Value: " + Float.toString((Float) series1Numbers[0]));
        //Log.d("plt", "Current Display Sensor: " + currentDisplaySensor);
        //Log.d("plt","should be showing a value");

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "X data");
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Y data");
        XYSeries series3 = new SimpleXYSeries(
                Arrays.asList(series3Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Z data");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.RED, null, null);
        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.BLUE, Color.BLUE, null, null);
        LineAndPointFormatter series3Format = new LineAndPointFormatter(Color.GREEN, Color.GREEN, null, null);


        if (plot !=null){
            plot.clear();

            // add a new series' to the xyplot:
            plot.addSeries(series1, series1Format);
            plot.addSeries(series2, series2Format);
            plot.addSeries(series3, series3Format);


            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int i = Math.round(((Number) obj).floatValue());
                    return toAppendTo.append(domainLabels[i]);
                }

                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });
            plot.redraw();
        }
    }

    private void initspinnerfooter() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, SensorList);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);

                currentDisplaySensor=(String) parent.getItemAtPosition(position);
                RecordingActivity activity = (RecordingActivity) getActivity();



             }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
}

}
