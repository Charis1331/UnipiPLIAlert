package com.example.xoulis.xaris.unipiplialert;


import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

class SendSMS {

    private static final String GOOGLE_MAPS_URI = "https://www.google.com/maps/search/";
    private static final String SEND_SMS_WITH_LOCATION = "with_location";
    private static final String SEND_SMS_WITHOUT_LOCATION = "without_location";

    static void checkForPermissions(final AppCompatActivity activity) {
        HandlePermissions.areLocationSettingsMet(activity);
        HandlePermissions.askForSmsAndPhoneStatePermissions(activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HandlePermissions.hasLocationPermissionBeenGranted(activity) &&
                        HandlePermissions.locationSettingsAreMet &&
                        HandlePermissions.haveSmsAndPhoneStatePermissionsBeenGranted(activity)) {
                    sendTheSms(activity, SEND_SMS_WITH_LOCATION);
                } else if (HandlePermissions.haveSmsAndPhoneStatePermissionsBeenGranted(activity)) {
                    sendTheSms(activity, SEND_SMS_WITHOUT_LOCATION);
                } else {
                    Toast.makeText(activity, "No permissions were granted!", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            }
        }, 5000);

    }

    private static void sendTheSms(AppCompatActivity activity, String mode) {
        // Get the sms manager
        SmsManager smsManager = SmsManager.getDefault();

        // Create the pending intents to control the result
        PendingIntent pendingIntentSend = PendingIntent.getBroadcast(activity, 0,
                new Intent(Intent.ACTION_VIEW), 0);
        PendingIntent pendingIntentDelivered = PendingIntent.getBroadcast(activity, 0,
                new Intent(Intent.ACTION_VIEW), 0);

        String message;

        if (mode.equals(SEND_SMS_WITH_LOCATION)) {
            // Update user's last location coordinates
            ConfigureGPS.getUserLastLocation(activity);
            double latitude = ConfigureGPS.latitude;
            double longitude = ConfigureGPS.longitude;

            // Construct the message to be sent
            Uri baseUri = Uri.parse(GOOGLE_MAPS_URI);
            Uri.Builder builder = baseUri.buildUpon();
            builder.appendQueryParameter("api", "1");
            builder.appendQueryParameter("query", latitude + ", " + longitude);
            message = activity.getString(R.string.sms_text_with_location) + builder.toString();
        } else {
            message = activity.getString(R.string.sms_text_without_location);
        }

        smsManager.sendTextMessage("+301234567890",
                null,
                message,
                pendingIntentSend,
                pendingIntentDelivered);
    }
}
