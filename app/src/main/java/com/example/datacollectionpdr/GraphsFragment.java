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
import android.app.Activity;
import android.graphics.*;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;

public class GraphsFragment extends Fragment {

    public GraphsFragment() {
        // required empty public constructor.
    }
    XYPlot plot;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graphs, container, false);
        // initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.plot);
        Log.e("plt","im here");

        RecordingActivity activity = (RecordingActivity) getActivity();
        Number[] DataArray1 = activity.getMyData();
        Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
        Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};;

        PlotThePlot(domainLabels,DataArray1,series2Numbers);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PixelUtils.init(getActivity());
        //

    }

    public void PlotThePlot(Number[] domainLabels,Number[] series1Numbers,Number[] series2Numbers){

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);


        if (plot !=null){
            Log.e("plt","im here");
            // add a new series' to the xyplot:
            plot.addSeries(series1, series1Format);
            plot.addSeries(series2, series2Format);

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
        }
    }
}
