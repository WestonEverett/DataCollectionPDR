package com.example.datacollectionpdr;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;


import android.content.Intent;

import com.example.datacollectionpdr.datacollectionandpreparation.DataCollection;
import com.example.datacollectionpdr.datacollectionandpreparation.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends DataManager implements DataCollection.OnMotionSensorManagerListener{

    private DataCollection mMotionSensorManager;

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMotionSensorManager = new DataCollection(this);
        mMotionSensorManager.setOnMotionSensorManagerListener(this);


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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {

                if(grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMotionSensorManager.registerMotionSensors();
    }

    @Override
    protected void onPause(){
        super.onPause();

        mMotionSensorManager.unregisterMotionSensors();
    }
}