package com.example.datacollectionpdr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ServerIDentered extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_identered);

        Intent intent = getIntent();
        String message = intent.getStringExtra(About.EXTRA_MESSAGE);

        TextView textView = findViewById(R.id.textView_serverID);
        textView.setText(message);
    }

    public void BackButton(View view) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent); //Go to the Show Help activity and its view

    }

    public void ConnectButton (View view) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent); //Go to the Show Help activity and its view{
    }
}