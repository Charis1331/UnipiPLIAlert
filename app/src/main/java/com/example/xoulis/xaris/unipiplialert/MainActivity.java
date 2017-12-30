package com.example.xoulis.xaris.unipiplialert;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.xml.datatype.Duration;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SensorEventListener {

    private ProgressBar progressBar;
    private Button sosButton;
    private CountDownTimer timer;

    private static final int SECONDS_UNTIL_SMS = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialise the Views
        initViews();

        // Set the Listener for the SOS button
        initListener();

        // Start the Welcome Intro ONLY the first time the app launches
        if (SettingsPreferences.getFirstTimeStart(this)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }

        SensorManager sens = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sens.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sens.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

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

    private void initTimer() {
        progressBar.setVisibility(View.VISIBLE);
        String initialText = SECONDS_UNTIL_SMS + "s";
        sosButton.setText(initialText);
        startTimer(SECONDS_UNTIL_SMS);
    }

    private void stopTimer() {
        progressBar.setVisibility(View.INVISIBLE);
        sosButton.setText(getString(R.string.button_text));
        timer.cancel();
    }

    private void startTimer(long timeInSec) {
        timer = new CountDownTimer(timeInSec * 1000 + 100, 1000) {
            @Override
            public void onTick(long l) {
                int secondsLeft = (int) (l / 1000);
                progressBar.setProgress(secondsLeft);
                String text = secondsLeft + "s";
                sosButton.setText(text);
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(SECONDS_UNTIL_SMS);
            }
        }.start();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        sosButton = findViewById(R.id.sosButton);
    }
    private Toast t;

    private void initListener() {
        sosButton.setOnClickListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor currentSensor = sensorEvent.sensor;
        if (currentSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            //Log.i("VALUEX",x+"");
            //Log.i("VALUEY",y+"");
            //Log.i("VALUEZ",z+"");

            double accelerationFormula = Math.sqrt(Math.pow(x,2)
                    + Math.pow(y,2) + Math.pow(z,2));



            if (accelerationFormula > 20) {
                Log.i("Value", accelerationFormula+"");
                if (t != null){
                    t.cancel();
                }
                t = Toast.makeText(this, "FALL!!", Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
