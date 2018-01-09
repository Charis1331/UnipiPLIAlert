package com.example.xoulis.xaris.unipiplialert;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {

    private boolean hasSlide3BeenCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add the slides
        addSlide(SampleSlide.newInstance(R.layout.slide1));
        addSlide(SampleSlide.newInstance(R.layout.slide2));
        addSlide(SampleSlide.newInstance(R.layout.slide3));

        // Request permissions after 2nd slide
        askForPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE}, 2);

        showSkipButton(false);
        setSeparatorColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    protected void onPageSelected(int position) {
        super.onPageSelected(position);

        // Handle Slide3 on Slide3.java class
        if (position == 2 && !hasSlide3BeenCreated) {
            setProgressButtonEnabled(false);
            Slide3 obj = new Slide3(this);
            obj.configureSlide3();
            hasSlide3BeenCreated = true;
        }
    }
}
