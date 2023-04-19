package com.example.datacollectionpdr;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;


/** class defining the fragment that displays live sensor data during the recording
 * it sets one of the fragments shown from the during_recording activity and uses plotXY
 * library for producing the graph. Sensor values are accesed through DataViewModel class
 *
 *  * Author: Dagna Wojciak, Weston Everett
 *  * Affiliation: The University of Edinburgh
 */
public class GraphsFragment extends Fragment {

    String[] SensorList = { "Accelerometer", "Gyroscope", "Rotation", "Barometer"}; //list of possible sensors
    XYPlot plot;                                    //initialise data plot from plotXY
    Spinner dropdown;                               //initialise dropdown menu
    String currentDisplaySensor;                    //initialise string with the currently selected sensor
    private RecentMeasurements recentMeasurements;  //initialise variable to hold values to be displayed on the graph
    int graphPointsNum = 200;                       //number of samples to be plotted

    public GraphsFragment() {
        // required empty public constructor.
    }

    /**define initial states of plot and  menu */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recentMeasurements = new RecentMeasurements(graphPointsNum);   //set how much data ets plotted
        currentDisplaySensor = "Accelerometer";                         //set initial sensor to be plotted
        PixelUtils.init(requireActivity());                             //ensure the plot can be created with PixelUtils library
    }

    /**sets up the view from fragment_graphs layout file and initialises the dropdown menu and graph
     * returns the fragment view
     */
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_graphs, container, false);   //set up layout
        plot = (XYPlot) view.findViewById(R.id.plot); // initialize XYPlot reference:
        dropdown = view.findViewById(R.id.spinner2);// initialize drop down menureference:
        initspinnerfooter();                        //set up the menu

        return view;
    }

    /**after view is set up, prepare the graph from pltting and compile the initial samples plot
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get measurements from DataViewModel
        DataViewModel viewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        //set up sensor samples to be updated when new measurements are ready
        viewModel.getMotionSample().observe(getViewLifecycleOwner(), item -> {
            recentMeasurements.updateMeasurements(item);
            PlotThePlot(recentMeasurements.getData(currentDisplaySensor));  //when new data available call function that plots the data
        });

        //set up pressure sensor samples to be updated when new measurements are ready
        //Note pressure sensor is not part of the motionSample above
        viewModel.getPressure().observe(getViewLifecycleOwner(), item -> {
            recentMeasurements.updateMeasurements(item);
        });
    }

    /** Function that plots a graph of measurements of the given sensor with a given numer of previous
     * samples plotted. Any number of series can be plotted depending on the sensor
     * @param plotData - data to be plotted (taken from sensor readings)
     */
    public void PlotThePlot(Number[][] plotData){

        Number[] domainLabels = new Number[graphPointsNum]; //initialise the x axis labels
        for(long i = 0; i <= plotData.length; i++){         //set the labels to be sample numbers
            domainLabels[(int)i]=i;
        }

        //define array list so that any number of data series can  be displayed at once
        ArrayList<SimpleXYSeries> series = new ArrayList<>();
        ArrayList<LineAndPointFormatter> seriesFormats = new ArrayList<>();
        String[] titles = new String[]{"time", "X data", "Y data", "Z data", "", "", ""};
        int[] colors = new int[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN};

        //display all available series of data with different colours
        for(int i = 1; i < plotData.length; i++){
            series.add(new SimpleXYSeries(Arrays.asList(plotData[i]), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, titles[i]));
            seriesFormats.add(new LineAndPointFormatter(colors[i], colors[i], null, null));
        }

        //clear the plot window of any previous plots
        if (plot !=null){
            plot.clear();

            //plot all the data on the graph using series and format pairs
            for (int i = 0; i < series.size(); i++){
                plot.addSeries(series.get(i), seriesFormats.get(i));
            }

            //format the graph - set up x axis labels
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
            plot.redraw();  //redraw the plot with new values
        }
    }

    /**function that sets up the drop down menu allowing user to pick the sensor which data is displayed
     */
    private void initspinnerfooter() {

        //get the layout element
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, SensorList);
        dropdown.setAdapter(adapter);   //set up view adater

        //when a new item is selected from the menu
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));     //check which item choosen
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);   //display the selected item when drop down menu rolled up

                currentDisplaySensor=(String) parent.getItemAtPosition(position);   //update the string indicating the choosen sensor
            }

            //set the default seected sensor
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentDisplaySensor = "Accelerometer";
            }
        });
    }
}
