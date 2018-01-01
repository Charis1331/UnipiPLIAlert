package com.example.xoulis.xaris.unipiplialert;


import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;

public class SendSMS {

    private static final String GOOGLE_MAPS_URI = "https://www.google.com/maps/search/";
    private static final String SEND_SMS_WITH_LOCATION = "with_location";
    private static final String SEND_SMS_WITHOUT_LOCATION = "without_location";

    protected static void checkForPermissions(final AppCompatActivity activity) {
        // Check if location permission has been granted
        if (HandlePermissions.hasLocationPermissionBeenGranted(activity)) {
            // Check for current location settings
            HandlePermissions.areLocationSettingsMet(activity);

            // Location settings are met
            if (HandlePermissions.locationSettingsAreMet) {
                sendTheSms(activity, SEND_SMS_WITH_LOCATION);

                // Location settings are NOT met
            } else {
                // Check, after a while(3 sec), if user has accepted our Location Settings or not
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Location Settings are met
                        if (HandlePermissions.locationSettingsAreMet) {
                            sendTheSms(activity, SEND_SMS_WITH_LOCATION);
                            // Location Settings are NOT met, send SMS without location
                        } else {
                            sendTheSms(activity, SEND_SMS_WITHOUT_LOCATION);
                        }
                    }
                }, 3000);
            }

            // Location permission hasn't been already granted
        } else {
            HandlePermissions.askForLocationPermission(activity);
            if (HandlePermissions.hasLocationPermissionBeenGranted(activity)) {
                sendTheSms(activity, "without_location");
            } else {
                // TODO steile minima xwris location
            }
        }
    }

    private static void sendTheSms(AppCompatActivity activity, String mode) {
        // Get the sms manager
        SmsManager smsManager = SmsManager.getDefault();

        // Create the pending intents to control the result
        PendingIntent pendingIntentSend = PendingIntent.getBroadcast(activity, 0,
                new Intent(Intent.ACTION_VIEW), 0);
        PendingIntent pendingIntentDelivered = PendingIntent.getBroadcast(activity, 0,
                new Intent(Intent.ACTION_VIEW), 0);


        Uri baseUri = Uri.parse(GOOGLE_MAPS_URI);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("api", "1");
        builder.appendQueryParameter("query", "37.888805, 23.765799");

        String finalUrl = builder.toString();

        smsManager.sendTextMessage("+301234567890",
                null,
                finalUrl,
                pendingIntentSend,
                pendingIntentDelivered);
    }
}
