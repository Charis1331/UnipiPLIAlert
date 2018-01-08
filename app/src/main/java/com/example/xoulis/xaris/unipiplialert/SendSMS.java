package com.example.xoulis.xaris.unipiplialert;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.Toast;

class SendSMS {

    static BroadcastReceiver smsBroadcastReceiver;

    private static final String GOOGLE_MAPS_URI = "https://www.google.com/maps/search/";
    private static final String SEND_SMS_WITH_LOCATION = "with_location";
    private static final String SEND_SMS_WITHOUT_LOCATION = "without_location";
    static final String SMS_SENT = "sms_sent";
    static final String ABORT_MODE = "abort_mode";
    static final String REGULAR_MODE = "regular_mode";

    static void configureTheSms(final AppCompatActivity activity, String mode) {
        // Check for location, sms and phone permissions
        HandlePermissions.areLocationSettingsMet(activity);
        HandlePermissions.askForSmsAndPhoneStatePermissions(activity);

        // Setup the SMS Broadcast Receiver
        setupSmsReceiver(activity);

        if (mode.equals(REGULAR_MODE)) {

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

        } else {
            if (HandlePermissions.haveSmsAndPhoneStatePermissionsBeenGranted(activity)) {
                sendTheSms(activity, ABORT_MODE);
            }
        }
    }

    private static void setupSmsReceiver(final AppCompatActivity activity) {
        smsBroadcastReceiver = new BroadcastReceiver() {
            Toast toast;

            @Override
            public void onReceive(Context context, Intent intent) {
                String messageToShow;
                switch (getResultCode()) {
                    case AppCompatActivity.RESULT_OK:
                        messageToShow = activity.getString(R.string.message_sent_toast);
                        break;
                    default:
                        messageToShow = activity.getString(R.string.message_not_sent_toast);
                        break;
                }

                // Show the toast only one time
                if (toast == null) {
                    toast = Toast.makeText(context, messageToShow, Toast.LENGTH_SHORT);
                    toast.show();
                }

                // Unregister the smsBroadcastReceiver
                if (SendSMS.smsBroadcastReceiver != null) {
                    activity.unregisterReceiver(SendSMS.smsBroadcastReceiver);
                }
            }
        };
    }

    private static void sendTheSms(final AppCompatActivity activity, String mode) {
        // Get the sms manager
        SmsManager smsManager = SmsManager.getDefault();

        // Create the pending intents to control the result
        PendingIntent pendingIntentSend = PendingIntent.getBroadcast(activity, 0,
                new Intent(SMS_SENT), 0);
        PendingIntent pendingIntentDelivered = PendingIntent.getBroadcast(activity, 0,
                new Intent(Intent.ACTION_VIEW), 0);

        // Declare message and initialise the contact numbers
        String message;
        String contact1 = SettingsPreferences.getContact1(activity);
        String contact2 = SettingsPreferences.getContact2(activity);

        switch (mode) {
            case SEND_SMS_WITH_LOCATION:
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
                break;
            case SEND_SMS_WITHOUT_LOCATION:
                message = activity.getString(R.string.sms_text_without_location);
                break;
            default:
                message = activity.getString(R.string.abort_sms_text);
                break;
        }

        // Send the message to the contacts
        smsManager.sendTextMessage(contact1,
                null,
                message,
                pendingIntentSend,
                pendingIntentDelivered);

        smsManager.sendTextMessage(contact2,
                null,
                message,
                pendingIntentSend,
                pendingIntentDelivered);
    }
}