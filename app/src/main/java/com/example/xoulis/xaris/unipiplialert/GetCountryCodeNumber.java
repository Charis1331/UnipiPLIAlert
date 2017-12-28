package com.example.xoulis.xaris.unipiplialert;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GetCountryCodeNumber {

    public static String getCallCode(Context context) {

        // Pass in the array with all the country codes
        String[] allCodes = context.getResources().getStringArray(R.array.CountryCodes);
        String countryCallCode = "";

        // Find the device's SIM country code
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceCountryCode = tm.getSimCountryIso().toUpperCase();
        Log.i("CountryCode", deviceCountryCode);

        // Search in the array for the country call code
        for (String code : allCodes) {
            String[] splittedCode = code.split(",");
            if (splittedCode[1].trim().equals(deviceCountryCode.trim())) {
                countryCallCode = splittedCode[0];
                break;
            }
        }
        return countryCallCode;
    }
}
