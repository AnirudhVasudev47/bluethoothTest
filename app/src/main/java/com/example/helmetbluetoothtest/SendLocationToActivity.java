package com.example.helmetbluetoothtest;

import android.location.Location;

public class SendLocationToActivity {

    public Location location;

    public SendLocationToActivity(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
