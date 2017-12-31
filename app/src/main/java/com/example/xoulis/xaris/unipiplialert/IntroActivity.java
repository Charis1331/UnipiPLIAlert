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

        addSlide(SampleSlide.newInstance(R.layout.slide1));
        addSlide(SampleSlide.newInstance(R.layout.slide2));
        addSlide(SampleSlide.newInstance(R.layout.slide3));

        askForPermissions(new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        showSkipButton(false);
        //setProgressButtonEnabled(false);
        setSeparatorColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    protected void onPageSelected(int position) {
        super.onPageSelected(position);

        if (position == 2 && !hasSlide3BeenCreated) {
            Slide3 obj = new Slide3(this);
            obj.configureSlide3();
            hasSlide3BeenCreated = true;
        }
    }
}
