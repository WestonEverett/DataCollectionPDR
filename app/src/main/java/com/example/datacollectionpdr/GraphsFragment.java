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

import com.androidplot.Series;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import com.androidplot.xy.XYPlot;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;

public class GraphsFragment extends Fragment {

    String[] SensorList = { "Accelerometer", "Gyroscope",
            "Rotation", "Barometer"};

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

        viewModel.getMotionSample().observe(getViewLifecycleOwner(), item -> {
            recentMeasurements.updateMeasurements(item);
            PlotThePlot(recentMeasurements.getData(currentDisplaySensor));
        });

        viewModel.getPressure().observe(getViewLifecycleOwner(), item -> {
            recentMeasurements.updateMeasurements(item);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recentMeasurements = new RecentMeasurements(200);
        currentDisplaySensor = "Accelerometer";
        PixelUtils.init(getActivity());
    }

    public void PlotThePlot(Number[][] plotData){

        Number[] domainLabels=plotData[0];

        for(int i = 0; i <= plotData.length; i++){
            domainLabels[i]=i;
        }
        //Number[] domainLabels=plotData[0];


        ArrayList<SimpleXYSeries> series = new ArrayList<>();
        ArrayList<LineAndPointFormatter> seriesFormats = new ArrayList<>();
        String[] titles = new String[]{"time", "X data", "Y data", "Z data"};
        int[] colors = new int[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN};

        for(int i = 1; i < plotData.length; i++){
            series.add(new SimpleXYSeries(Arrays.asList(plotData[i]), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, titles[i]));
            seriesFormats.add(new LineAndPointFormatter(colors[i], colors[i], null, null));
        }

        if (plot !=null){
            plot.clear();

            for (int i = 0; i < series.size(); i++){
                plot.addSeries(series.get(i), seriesFormats.get(i));
            }

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
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

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
