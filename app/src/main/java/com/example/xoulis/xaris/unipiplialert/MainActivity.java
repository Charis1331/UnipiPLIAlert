package com.example.xoulis.xaris.unipiplialert;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SensorEventListener {

    private ProgressBar progressBar;
    private Button sosButton;

    private CountDownTimer timer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ToneGenerator toneGen;

    private boolean locationPermissionWasGranted;

    private static final int SECONDS_UNTIL_SMS = 30;

    /* ---------------------- ACTIVITY LIFECYCLE METHODS ---------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the Welcome Intro ONLY the first time the app launches
        if (SettingsPreferences.getFirstTimeStart(this)) {
            Intent intent = new Intent(this, IntroActivity.class);
            // TODO
            //startActivity(intent);
        }

        // GPS
        HandlePermissions.areLocationSettingsMet(this);

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
        // Unregister the sensorManager
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        // Cancel the timer and release the ToneGenerator
        if (timer != null) {
            stopTimer();
            toneGen.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            registerSensorManager();
        }
    }

    /* ---------------------- INITIALISATION METHODS ---------------------- */

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        sosButton = findViewById(R.id.sosButton);
    }

    private void initSens() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initListeners() {
        sosButton.setOnClickListener(this);
        registerSensorManager();
    }

    private void registerSensorManager() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
        // Initialise ToneGenerator
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        timer = new CountDownTimer(timeInSec * 1000 + 100, 1000) {
            @Override
            public void onTick(long l) {
                int secondsLeft = (int) (l / 1000);
                progressBar.setProgress(secondsLeft);
                toneGen.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 150);
            }

            @Override
            public void onFinish() {
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
        registerSensorManager();
    }

    /* ---------------------- SEND SMS ---------------------- */

    private void sendSms() {
        // Check for location permission

    }

    /* ---------------------- LISTENERS ---------------------- */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sosButton) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                stopTimer();
            } else {
                initTimer();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor currentSensor = sensorEvent.sensor;
        if (currentSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            double accelerationFormula = Math.sqrt(Math.pow(x, 2)
                    + Math.pow(y, 2) + Math.pow(z, 2));

            if (accelerationFormula > 20) {
                initTimer();
                sensorManager.unregisterListener(this);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /* ---------------------- PERMISSION HANDLING ---------------------- */
    // TODO make them return true or false!s
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionWasGranted = HandlePermissions.
                onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        HandlePermissions obj = new HandlePermissions();
        obj.onActivityResult(requestCode, resultCode, data, this);
    }
}
