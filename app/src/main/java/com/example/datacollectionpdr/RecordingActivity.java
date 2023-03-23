package com.example.datacollectionpdr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;

public class RecordingActivity extends DataManager {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
    }

    /** method linking to the help view triggered by a button */
    public void stopRecording(View view){
        Intent intent = new Intent(this, RecordingReview.class);
        startActivity(intent); //Go to the Show Help activity and its view
    }

}