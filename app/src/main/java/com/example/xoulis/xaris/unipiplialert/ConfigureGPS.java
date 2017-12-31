package com.example.xoulis.xaris.unipiplialert;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;

class ConfigureGPS {

    static LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    // Update location every 20 minutes
    private static final long LOCATION_UPDATE_INTERVAL_IN_SECONDS = TimeUnit.MINUTES.toMillis(20);

    void configureFusedLocationClient(AppCompatActivity activity) {
        // Setup the location request
        setupLocationRequest();

        // Check for location permission
        HandlePermissions obj = new HandlePermissions();
        obj.handleLocationPermission(activity);
    }

    private void setupLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL_IN_SECONDS);
        //locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    void startLocationUpdate(AppCompatActivity activity) {
        // Create the callback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    Log.i("LOCANOW33", latitude + " " + longitude);
                }
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @SuppressLint("MissingPermission")
    void getUserLastLocation(AppCompatActivity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();
                            Log.i("LASTKNO", latitude + " " + longitude);
                        }
                    }
                });
    }
}
