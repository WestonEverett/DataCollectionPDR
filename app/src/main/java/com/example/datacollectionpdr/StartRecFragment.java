package com.example.datacollectionpdr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;


public class StartRecFragment extends Fragment implements View.OnClickListener {

    View view;

    public StartRecFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_start_rec, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // find views by id
        Button sendButton = (Button) view.findViewById(R.id.button_startRec);
        Button server_apiButton = (Button) view.findViewById(R.id.enter_server_api);
        sendButton.setOnClickListener(this);
        server_apiButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.button_startRec:
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView_recording_activity, new duringRecordingFragment());
                ((RecordingActivity) getActivity()).startRecording();
                fragmentTransaction.commit();
                break;
        }
    }
}