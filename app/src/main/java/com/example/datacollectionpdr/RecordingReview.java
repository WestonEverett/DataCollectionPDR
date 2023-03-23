package com.example.datacollectionpdr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RecordingReview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_review);
    }

    /** method linking to the help view triggered by a button */
    public void discardRecording(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent); //Go to the Show Help activity and its view
    }

    /** method linking to the help view triggered by a button */
    public void saveRecording(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent); //Go to the Show Help activity and its view
    }



}