package team58.cs2340.donationtracker.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import team58.cs2340.donationtracker.Models.Location;
import team58.cs2340.donationtracker.Models.LocationManager;
import team58.cs2340.donationtracker.Models.Model;
import team58.cs2340.donationtracker.Models.User;
import team58.cs2340.donationtracker.Models.UserManager;
import team58.cs2340.donationtracker.R;

public class LocationList extends AppCompatActivity{

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.locationManager = LocationManager.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationlist);
        ListView locationListView = findViewById(R.id.locationList);
        LocationListAdapter locationAdapter = new LocationListAdapter(this, R.layout.layout_locationitem, (ArrayList<Location>) locationManager.getLocations());
        locationListView.setAdapter(locationAdapter);

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LocationList.this, PageLocation.class);
                intent.putExtra("location", locationManager.getLocations().get(position));
                startActivity(intent);
            }
        });

    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.logoutBtn:
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(this, Welcome.class);
                startActivity(logoutIntent);
                Toast.makeText(getApplicationContext(), "Logout Successful!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }


}
