package com.example.xoulis.xaris.unipiplialert;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

class HandlePermissions {

    private static final int REQUEST_LOCATION_PERMISSIONS = 12;
    private static final int REQUEST_CHECK_SETTINGS = 13;

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, AppCompatActivity activity) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleCurrentLocationSetting(ConfigureGPS.locationRequest, activity);
                } else {
                    // Permission denied
                    activity.finish();
                }
                return;
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data, AppCompatActivity activity) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                ConfigureGPS obj = new ConfigureGPS();
                obj.startLocationUpdate(activity);
            } else {
                //activity.finish();
            }
        }
    }

    /* ---------------------- LOCATION PERMISSIONS ---------------------- */

    void handleLocationPermission(AppCompatActivity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSIONS);

        } else {
            // Permission has already been granted, so check current location settings
            handleCurrentLocationSetting(ConfigureGPS.locationRequest, activity);
        }
    }

    private void handleCurrentLocationSetting(LocationRequest locationRequest, final AppCompatActivity activity) {
        // Get current location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // Check if they (the location settings) are satisfied
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i("HEYY", "shit");
                ConfigureGPS obj = new ConfigureGPS();
                obj.startLocationUpdate(activity);
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

}
