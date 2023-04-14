package com.example.datacollectionpdr;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.datacollectionpdr.nativedata.PDRStep;
import java.util.ArrayList;


/**
 * Class managing UI display elements in particular trajectory plots
 * Author:  Weston Everett
 * Affiliation: The University of Edinburgh
 */
public class UITools {

    /**
     * Function that plots pdr trajectory using a XYPlot object
     * @param steps - coordinates to be plotted
     * @param color - color for the plot
     * @param plot - XYPlot view object
     */
    public static void plotPDRTrajectory(ArrayList<PDRStep> steps, int color, XYPlot plot){

        ArrayList<Number> xVals = new ArrayList<>();    //list of x coordinates
        ArrayList<Number> yVals = new ArrayList<>();    //list of y coordinates

        //set initial location as 0,0
        float curX = 0;
        float curY = 0;

        //add the initial coordinates to the list (0,0)
        xVals.add(curX);
        yVals.add(curY);

        //calculate absolute coordinate versus the start point using recursive formula for all available steps
        for(PDRStep step : steps){
            curX = curX + step.getX();
            curY = curY + step.getY();

            xVals.add(curX); //add to coordinates list
            yVals.add(curY); //add to coordinates list
        }

        //create a dataseries for the plot
        SimpleXYSeries series = new SimpleXYSeries(xVals, yVals, "Estimated Location");
        //format the series plot
        LineAndPointFormatter series1Format = new LineAndPointFormatter(color, color, null, null);

        //if plot view exist then display the series
        if (plot !=null){
            plot.clear();
            // add a new series' to the xy plot:
            plot.addSeries(series, series1Format);

            plot.redraw();  //refresh the plot
        }
    }
}
