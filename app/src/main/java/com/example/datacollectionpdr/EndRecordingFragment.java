package com.example.datacollectionpdr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;


public class EndRecordingFragment extends Fragment implements View.OnClickListener{

    View view;

    public EndRecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_end_recording, container, false);
        Button sendButton = (Button) view.findViewById(R.id.button_send);
        sendButton.setOnClickListener(this);
        Button server_apiButton = (Button) view.findViewById(R.id.enter_server_api);
        server_apiButton.setOnClickListener(this);
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
            case R.id.button_send:
                Intent intent_send = new Intent(getActivity(), MainActivity.class);
                startActivity(intent_send); //Go to the Show Help activity and its view
                ((Activity) getActivity()).overridePendingTransition(0, 0);
                break;
            case R.id.enter_server_api:
                TextInputLayout textInputLayout = view.findViewById(R.id.textInput_serverid);
                String text = textInputLayout.getEditText().getText().toString();
                MainActivity.serverKeyString=text;
                break;
        }
    }
}