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
import com.example.datacollectionpdr.serializationandserver.FileManager;
import com.example.datacollectionpdr.serializationandserver.ServerManager;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.Objects;

/**
Fragment to show saved and corrected trajectory to the user and give option to save/send/discrad
 */
public class ReviewFragment extends Fragment  implements View.OnClickListener {

    View view;                          //View of the fragment
    Button sendButton;                  //Send Button
    Button saveButton;                  //Save Button
    Button discardButton;               //Button to discard recording
    Button enterTextButton;             //Button to set server api from text input
    Button enterFileNameButton;         //Button to set
    XYPlot plot;                        //PDR visual representation
    TrajectoryNative trajectoryNative;  //object to access PDR data

    public ReviewFragment() { // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    /**
     When view for the fragment is created initialise all the views, buttons etc.
     input: saved state of the activity
     output: view from fragment review layout with all the buttons,text inputs etc.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Set up fragment view
        view = inflater.inflate(R.layout.fragment_review, container, false);

        sendButton = (Button) view.findViewById(R.id.button_send);      //Initialise Send button
        sendButton.setOnClickListener(this);                            //Set up Listener

        saveButton = (Button) view.findViewById(R.id.button_save);      //Initialise Save button
        saveButton.setOnClickListener(this);                            //Set up Listener

        discardButton = (Button) view.findViewById(R.id.button_discard);//Initialise Discard button
        discardButton.setOnClickListener(this);                         //Set up Listener

        enterTextButton = (Button) view.findViewById(R.id.button_entertext);//Initialise enter button for server key text input
        enterTextButton.setOnClickListener(this);                           //Set up Listener

        enterFileNameButton = (Button) view.findViewById(R.id.button_entertext_file); //Initialise enter button for file name text input
        enterFileNameButton.setOnClickListener(this);                                 //Set up Listener

        TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);//Initialise server id text input
        textInputLayout.setHint("Current ID: "+ MainActivity.serverKeyString);      //set hint text

        TextInputLayout textInputLayoutFile = view.findViewById(R.id.textInput_filename);  //Initialise file name text input
        textInputLayoutFile.setHint("Suggested Name: "+ MainActivity.fileNameString);      //set hint text

        trajectoryNative = ((RecordingActivity) requireActivity()).trajectoryNative;            //Get trajectory
        plot = (XYPlot) view.findViewById(R.id.plot2);                                      //Initialise plotXY
        UITools.plotPDRTrajectory(trajectoryNative.getPdrs(), Color.RED, plot);             //plot trajectory from PDR

        return view; // Inflate the layout for this fragment
    }

    /**
     Do what you want to do when button is clicked
     input: fragment review view
     output: action depending on the button pressed
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {    //Choose action depending on which of the buttons pressed

            case R.id.button_discard:   //Discard button -> Go to main activiy without saving the file
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                ((Activity) requireActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.button_send:  //Send button -> Go to Send activity which sends the recording to server
                ServerManager.sendData(trajectoryNative, MainActivity.serverKeyString);
                Intent intent_send = new Intent(getActivity(), MainActivity.class);
                startActivity(intent_send);
                ((Activity) requireActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.button_save:  //Save button -> Go to Save activity which saves the recording to local file
                FileManager.createDataFile(getContext(), trajectoryNative);
                Intent intent_save = new Intent(getActivity(), MainActivity.class);
                startActivity(intent_save); //Go to the Show Help activity and its view
                ((Activity) requireActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.button_entertext: // Enter for the server id text -> update MainActivity.serverKeyString
                TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);
                MainActivity.serverKeyString = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
                textInputLayout.setHint("Current ID: " + MainActivity.serverKeyString);
                break;
            case R.id.button_entertext_file:    // Enter for file name text -> update MainActivity.fileNameString
                TextInputLayout textInputLayoutFile = view.findViewById(R.id.textInput_filename);
                MainActivity.fileNameString = Objects.requireNonNull(textInputLayoutFile.getEditText()).getText().toString();
                textInputLayoutFile.setHint("Chosen Name: " + MainActivity.fileNameString);
                break;
        }
    }
}