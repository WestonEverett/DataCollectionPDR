package com.example.datacollectionpdr;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import com.example.datacollectionpdr.datacollectionandpreparation.PermissionsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**Main class where the app is started - manages the home page layout - and checks for permissions*/
public class MainActivity extends PermissionsManager {

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    public static String serverKeyString= "6xJi8iwetoU6miQZyduemQ"; //String to store server ID (set to default at initialisation)
    public static String fileNameString="EnterFileName";    //String for filename
    //TODO do the default string automatically be given by rec time etc - in review fragment

    /**Initialise view and navigation items in the activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);     //set layout file
        getSupportActionBar().hide();               //hide app bar

        // Initialize and assign variable for bottom navigation bar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {   //when bottom navigation clicked

                switch(item.getItemId()) {
                    case R.id.dashboard:    //files page
                        startActivity(new Intent(getApplicationContext(),DashBoard.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.home:      //stay at home
                        return true;

                    case R.id.about:    //go to about page and activity
                            startActivity(new Intent(getApplicationContext(),About.class));
                            overridePendingTransition(0,0);
                            return true;
                }
                return false;   //in case a different id is passed - should be impossible
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

    /** turn off  listeners when paused */
    @Override
    protected void onPause(){
        //TODO turn the sensors etc off???
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
    }
}