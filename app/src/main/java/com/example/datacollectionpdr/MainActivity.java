package com.example.datacollectionpdr;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.content.Intent;

import com.example.datacollectionpdr.datacollectionandpreparation.DataCollection;
import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    BottomNavigationView bottomNavigationView;
    public static String serverKeyString= "6xJi8iwetoU6miQZyduemQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



                // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

                // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

                // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),DashBoard.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.home:
                        return true;

                    case R.id.about:
                            startActivity(new Intent(getApplicationContext(),About.class));
                            overridePendingTransition(0,0);
                            return true;
                }

                return false;
            }
        });
    }


    /** method linking to the help view triggered by a button */
    public void startRecording(View view){
        Intent intent = new Intent(this, RecordingActivity.class);
        startActivity(intent); //Go to the Show Help activity and its view
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}