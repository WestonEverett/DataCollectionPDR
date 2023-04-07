package com.example.datacollectionpdr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidplot.xy.XYPlot;
import com.example.datacollectionpdr.nativedata.TrajectoryNative;
import com.google.android.material.textfield.TextInputLayout;

/**
Fragment to show saved and corrected trajectory to the user and give option to save/send/discrad
 */
public class ReviewFragment extends Fragment  implements View.OnClickListener {

    View view;                  //View of the fragment
    Button sendButton;          //Send Button
    Button discardButton;      //Button to set Users start Location
    Button enterTextButton;      //Button to set Users start Location
    Button enterFileNameButton;
    XYPlot plot;
    TrajectoryNative trajectoryNative;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_review, container, false);

        sendButton = (Button) view.findViewById(R.id.button_review);
        sendButton.setOnClickListener(this);

        TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);
        textInputLayout.setHint("Current ID: "+ MainActivity.serverKeyString);

        enterTextButton = (Button) view.findViewById(R.id.button_entertext);
        enterTextButton.setOnClickListener(this);

        enterFileNameButton = (Button) view.findViewById(R.id.button_entertext_file);
        enterFileNameButton.setOnClickListener(this);

        TextInputLayout textInputLayoutFile = view.findViewById(R.id.textInput_filename);
        textInputLayoutFile.setHint("Suggested Name: "+ MainActivity.fileNameString);

        discardButton = (Button) view.findViewById(R.id.button_discard);
        discardButton.setOnClickListener(this);

        trajectoryNative = ((RecordingActivity) getActivity()).trajectoryNative;

        plot = (XYPlot) view.findViewById(R.id.plot2);
        UITools.plotPDRTrajectory(trajectoryNative.getPdrs(), Color.RED, plot);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.button_discard:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent); //Go to the Show Help activity and its view
                ((Activity) getActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.button_review:
                Intent intent_send = new Intent(getActivity(), MainActivity.class);
                startActivity(intent_send); //Go to the Show Help activity and its view
                ((Activity) getActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.button_entertext:
                TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);
                String text = textInputLayout.getEditText().getText().toString();
                MainActivity.serverKeyString = text;
                textInputLayout.setHint("Current ID: " + MainActivity.serverKeyString);
                break;
            case R.id.button_entertext_file:
                TextInputLayout textInputLayoutFile = view.findViewById(R.id.textInput_filename);
                String text_file = textInputLayoutFile.getEditText().getText().toString();
                MainActivity.fileNameString = text_file;
                textInputLayoutFile.setHint("Chosen Name: " + MainActivity.fileNameString);
                break;
        }
    }
}