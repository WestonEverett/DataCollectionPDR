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

            if(item.getEstFloor() != curFloor){
                curSteps = new ArrayList<>();
                curFloor = item.getEstFloor();
            }

            curSteps.add(item);
            UITools.plotPDRTrajectory(curSteps, plot);
            //TODO GRAPH
        });
    }
}
