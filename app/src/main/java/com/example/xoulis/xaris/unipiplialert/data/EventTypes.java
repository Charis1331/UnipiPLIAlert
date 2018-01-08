package com.example.xoulis.xaris.unipiplialert.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.example.xoulis.xaris.unipiplialert.data.EventsContract.EventsEntry;

public class EventTypes {

    public static final String FALL_EVENT = "fall_event";
    public static final String LIGHT_EVENT = "light_event";
    public static final String SOS_BUTTON_CLICK_EVENT = "sos_button_click";
    public static final String ABORT_SMS_EVENT = "abort_sms_sending_event";

    public static void addEventToDb(String eventType, Context context) {
        EventsDbHelper dbHelper = new EventsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(EventsEntry.COLUMN_TYPE_OF_EVENT, eventType);

        db.insert(EventsEntry.TABLE_NAME, null, cv);
    }
}
