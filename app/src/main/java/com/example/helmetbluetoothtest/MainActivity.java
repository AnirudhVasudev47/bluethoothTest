package com.example.helmetbluetoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;

    TextView info, distance, current;

    double lat;
    double lon;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    ArrayList<Double> latitude;
    ArrayList<Double> longitude;

    LocationManager locationManager;
    LocationListener locationListener;
    Location lastLocation;
    double distanceInMiles = 0;
    double distanceInKilometer = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        latitude.clear();
        longitude.clear();

        info = findViewById(R.id.info);
        distance = findViewById(R.id.distance);
        current = findViewById(R.id.current);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
//                Toast.makeText(LocationService.this, "Location:" + location.getLatitude() + location.getLongitude(), Toast.LENGTH_SHORT).show();
                current.setText("Current Location: " + location.getLatitude() + location.getLongitude());
                if (lastLocation == null) {
                    lastLocation = location;
                }


                distanceInMiles = calDistance(lastLocation, location);
                distanceInMiles = distanceInMiles * 1.609;
                distanceInKilometer = distanceInKilometer + distanceInMiles;
                lastLocation = location;
                DatabaseReference locationReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                locationReference.setValue(distanceInKilometer + " kms today");

                Toast.makeText(MainActivity.this, "Travelled: " + distanceInMiles, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        }

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("Users", dataSnapshot.getValue().toString());
                info.setText("snapshot Info:\n" + dataSnapshot.child("username").getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                info.setText(error.toString());
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }

    public void bluetoothCheck(View view) {
        Intent bluetoothIntent = new Intent(getApplicationContext(), bluetoothActivity.class);
        startActivity(bluetoothIntent);
    }

    public void travelled(View view) {

        distance.setText("Distance Travelled: " + distanceInKilometer);

    }

    private double calDistance(Location loc1, Location loc2) {

        double lat1 = loc1.getLatitude();
        double lon1 = loc1.getLongitude();
        double lat2 = loc2.getLatitude();
        double lon2 = loc2.getLongitude();

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}