package com.example.xoulis.xaris.unipiplialert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.xoulis.xaris.unipiplialert.TTS.MyTts;
import com.example.xoulis.xaris.unipiplialert.data.EventTypes;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SensorEventListener {

    private ProgressBar progressBar;
    private Button sosButton;

    private CountDownTimer timer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private ToneGenerator toneGen;

    private AlertDialog alertDialog;

    private String currentEmergency = USER_CLICK_EMERGENCY;

    private static final int SECONDS_UNTIL_SMS = 30;
    private static final double DANGEROUS_LUX_VALUE = 25000;
    private static final String ABORT_AFTER_FALL_DETECTED_PROCESS = "abort_after_fall_detected";
    private static final String ABORT_AFTER_USER_CLICK_PROCESS = "abort_after_user_click";
    private static final String FALL_EMERGENCY = "fall_detected";
    private static final String USER_CLICK_EMERGENCY = "user_clicked_sos_button";

    private MyTts tts;

    /* ---------------------- ACTIVITY LIFECYCLE METHODS ---------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise TTS obj
        tts = new MyTts(this);

        // Start the Welcome Intro ONLY the first time the app launches
        if (SettingsPreferences.getFirstTimeStart(this)) {
            Intent intent = new Intent(this, IntroActivity.class);
            // TODO
            //startActivity(intent);
        }

        // Initialise the Views
        initViews();

        //Initialise SensorManager and Sensors
        initSens();

        // Set the Listener for the SOS button
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Shutdown tts, if it is playing
        if (tts.isSpeaking()) {
            tts.stop();
            tts.shutdown();
        }

        // Unregister the sensorManager
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        // Cancel the timer and release the ToneGenerator
        if (timer != null) {
            stopTimer();
            toneGen.release();
        }

        // Stop location updates
        ConfigureGPS.stopLocationUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            registerSensorManager();
        }

        // Start location updates
        ConfigureGPS.configureFusedLocationClient(this);
    }

    /* ---------------------- INITIALISATION METHODS ---------------------- */

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        sosButton = findViewById(R.id.sosButton);
    }

    private void initSens() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    private void initListeners() {
        sosButton.setOnClickListener(this);
        registerSensorManager();
    }

    private void registerSensorManager() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /* ---------------------- ACTIVITY MENU ---------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* ---------------------- COUNTDOWN TIMER ---------------------- */

    private void initTimer() {
        progressBar.setVisibility(View.VISIBLE);
        sosButton.setText(getString(R.string.abort_text));
        startTimer(SECONDS_UNTIL_SMS);
    }

    private void startTimer(long timeInSec) {
        //obj.speak(getString(R.string.tts_message));

        // Turn up the device's volume to the max
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        // Initialise ToneGenerator
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        timer = new CountDownTimer(timeInSec * 1000 + 200, 1000) {
            @Override
            public void onTick(long l) {
                int secondsLeft = (int) (l / 1000);
                progressBar.setProgress(secondsLeft);

                if (secondsLeft >= 25) {
                    tts.speak(getString(R.string.tts_message));
                }

                if (secondsLeft < 25) {
                    tts.shutdown();
                    toneGen.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 150);
                }
            }

            @Override
            public void onFinish() {
                // Hide any active Alert Dialogs
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                stopTimer();
                sendSms();
            }
        }.start();
    }

    private void stopTimer() {
        progressBar.setProgress(SECONDS_UNTIL_SMS);
        progressBar.setVisibility(View.INVISIBLE);
        sosButton.setText(getString(R.string.sos_button_text));
        timer.cancel();
        toneGen.release();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /* ---------------------- SEND SMS ---------------------- */

    private void sendSms() {
        // Check for location permission
        SendSMS.configureTheSms(this, SendSMS.REGULAR_MODE);

        // Register the SMS Broadcast Receiver
        registerReceiver(SendSMS.smsBroadcastReceiver, new IntentFilter(SendSMS.SMS_SENT));
    }

    /* ---------------------- LISTENERS ---------------------- */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sosButton) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                if (currentEmergency.equals(FALL_EMERGENCY)) {
                    abortCurrentProcess(ABORT_AFTER_FALL_DETECTED_PROCESS);
                } else {
                    currentEmergency = USER_CLICK_EMERGENCY;
                    abortCurrentProcess(ABORT_AFTER_USER_CLICK_PROCESS);
                }
            } else {
                EventTypes.addEventToDb(EventTypes.SOS_BUTTON_CLICK_EVENT, this);
                initTimer();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int currentSensorType = sensorEvent.sensor.getType();
        if (currentSensorType == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            double accelerationFormula = Math.sqrt(Math.pow(x, 2)
                    + Math.pow(y, 2) + Math.pow(z, 2));

            if (accelerationFormula > 20) {
                currentEmergency = FALL_EMERGENCY;
                initTimer();
                EventTypes.addEventToDb(EventTypes.FALL_EVENT, this);
                sensorManager.unregisterListener(this, accelerometer);
            }
        } else if (currentSensorType == Sensor.TYPE_LIGHT) {
            float value = sensorEvent.values[0];
            if (value >= DANGEROUS_LUX_VALUE) {
                EventTypes.addEventToDb(EventTypes.LIGHT_EVENT, this);
                showAlertDialog();
                sensorManager.unregisterListener(this, lightSensor);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /* ---------------------- DISPLAY MESSAGES TO THE USER ---------------------- */

    private void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.alert_dialog_title));
        alertDialog.setMessage(getString(R.string.alert_dialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sensorManager.registerListener(MainActivity.this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private void confirmUserInfo() {
        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the layout inflater
        final LayoutInflater inflater = this.getLayoutInflater();

        // Pass the layout
        final View layout = inflater.inflate(R.layout.user_info_confirmation_dialog, null);

        // Set the layout
        builder.setView(layout)
                .setCancelable(false)
                .setPositiveButton(R.string.abort_text, null)
                .setNegativeButton("Cancel", null);

        // Show the dialog
        alertDialog = builder.create();
        alertDialog.show();

        // Set Listener for the buttons of the Dialog
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Find the editTexts of the alert dialog layout
                EditText username = layout.findViewById(R.id.confirmUsernameTextView);
                EditText password = layout.findViewById(R.id.confirmPasswordTextView);

                // Get their text
                String usernameEntered = username.getText().toString();
                String passwordEntered = password.getText().toString();

                if (usernameEntered.equals(SettingsPreferences.getUsername(MainActivity.this)) &&
                        passwordEntered.equals(SettingsPreferences.getPassowrd(MainActivity.this))) {
                    // Stop the timer
                    stopTimer();

                    // Send the "Abort" SMS
                    SendSMS.configureTheSms(MainActivity.this, SendSMS.ABORT_MODE);

                    // Write the event to the DB
                    EventTypes.addEventToDb(EventTypes.ABORT_SMS_EVENT, MainActivity.this);

                    // Hide the Alert Dialog
                    alertDialog.cancel();
                } else {
                    // Clear input fields
                    username.getText().clear();
                    password.getText().clear();

                    // Display wrong credentials toast
                    String toastMessage = getString(R.string.user_confirmation_dialog_wrong_input);
                    Toast toast = Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT);
                    if (toast != null) {
                        toast.cancel();
                        toast.show();
                    }
                }
            }
        });
    }

    /* ---------------------- ABORT PROCESS ---------------------- */

    private void abortCurrentProcess(String currentProcessType) {
        if (currentProcessType.equals(ABORT_AFTER_USER_CLICK_PROCESS)) {
            // Check for user credentials
            confirmUserInfo();
        } else {
            EventTypes.addEventToDb(EventTypes.ABORT_SMS_EVENT, this);
            stopTimer();
        }
    }

    /* ---------------------- PERMISSION HANDLING ---------------------- */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        HandlePermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        HandlePermissions obj = new HandlePermissions();
        obj.onActivityResult(requestCode, resultCode, data, this);
    }
}