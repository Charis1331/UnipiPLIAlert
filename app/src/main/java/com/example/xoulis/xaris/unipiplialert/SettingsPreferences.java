package com.example.xoulis.xaris.unipiplialert;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsPreferences {

    private static final String PREF_FIRST_TIME_START = "first_time_start";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_CONTACT1 = "contact1";
    private static final String PREF_CONTACT2 = "contact2";

    static boolean getFirstTimeStart(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return pref.getBoolean(PREF_FIRST_TIME_START, true);
    }

    static void setFirstTimeStart(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(PREF_FIRST_TIME_START, false);
        editor.apply();
    }

    public static void setUsername(Context context, String username) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_USERNAME, username);
        editor.apply();
    }

    public static void setPassword(Context context, String password) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_PASSWORD, password);
        editor.apply();
    }

    public static void setContact1(Context context, String contact1) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_CONTACT1, contact1);
        editor.apply();
    }

    public static void setContact2(Context context, String contact2) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_CONTACT2, contact2);
        editor.apply();
    }
}
