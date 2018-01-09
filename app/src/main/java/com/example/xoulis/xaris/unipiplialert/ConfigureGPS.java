package com.example.xoulis.xaris.unipiplialert;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

class ConfigureGPS {

    private static LocationCallback locationCallback;

    static LocationRequest locationRequest;
    static double latitude;
    static double longitude;

    // Update location every 20 minutes
    private static final long LOCATION_UPDATE_INTERVAL_IN_SECONDS = TimeUnit.MINUTES.toMillis(20);

    static void configureFusedLocationClient(final AppCompatActivity activity) {
        // Setup the location request
        setupLocationRequest();

        // Setup location callback
        createLocationCallback();

        // Check for location permission and settings
        HandlePermissions.askForLocationPermission(activity);
        HandlePermissions.areLocationSettingsMet(activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HandlePermissions.hasLocationPermissionBeenGranted(activity) &&
                        HandlePermissions.locationSettingsAreMet) {
                    // Start the location Updates
                    startLocationUpdates(activity);
                } else {
                    //activity.finish();
                }
            }
        }, 4000);
    }

    private static void setupLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL_IN_SECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private static void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private static void startLocationUpdates(AppCompatActivity activity) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    static void stopLocationUpdates(AppCompatActivity activity) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    static void getUserLastLocation(final AppCompatActivity activity) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
    }
}
