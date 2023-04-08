package com.example.datacollectionpdr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashBoard extends AppCompatActivity {

    /** on create function to set up the view for activity and initialise all the elements*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);    //Set up view from activity_dashboard xml

        getSupportActionBar().hide();   //Hide app bar

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Dashboard selected
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        // Perform Bottom navigation item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.dashboard:
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0,0);
                        return true;
                } return false;
            }
        });
    }

    //TODO Button to update files list

    //TODO to delete all files

    //TODO to get stuff from server

    //TODO list display for files

    //TODO Ideally add delete and show buttons - spearate fragment

    /**set up back button to go to home page*/
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        overridePendingTransition(0,0);
    }

}
