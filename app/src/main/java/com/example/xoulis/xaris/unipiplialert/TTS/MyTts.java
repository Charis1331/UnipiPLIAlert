package com.example.xoulis.xaris.unipiplialert.TTS;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class MyTts implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;

    public MyTts(Context context) {
        textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
        }
    }

    public void speak(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);
    }

    public void stop() {
        textToSpeech.stop();
    }

    public void shutdown() {
        textToSpeech.shutdown();
    }

    public boolean isSpeaking() {
        return textToSpeech.isSpeaking();
    }
}
