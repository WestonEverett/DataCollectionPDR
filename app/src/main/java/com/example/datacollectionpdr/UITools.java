package com.example.datacollectionpdr;

import android.graphics.Color;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.datacollectionpdr.nativedata.PDRStep;

import java.util.ArrayList;

public class UITools {

    public static void plotPDRTrajectory(ArrayList<PDRStep> steps, XYPlot plot){

        ArrayList<Number> xVals = new ArrayList<>();
        ArrayList<Number> yVals = new ArrayList<>();

        float curX = 0;
        float curY = 0;

        xVals.add(curX);
        yVals.add(curY);

        for(PDRStep step : steps){
            curX = curX + step.getX();
            curY = curY + step.getY();

            xVals.add(curX);
            yVals.add(curY);
        }

        SimpleXYSeries series = new SimpleXYSeries(xVals, yVals, null);

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.RED, null, null);

        if (plot !=null){
            plot.clear();


            plot.addSeries(series, new LineAndPointFormatter(Color.BLUE, null, null, null));

            // add a new series' to the xyplot:
            plot.addSeries(series, series1Format);

            plot.redraw();
        }
    }
}
