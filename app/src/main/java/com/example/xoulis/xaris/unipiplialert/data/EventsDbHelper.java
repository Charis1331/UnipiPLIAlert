package com.example.xoulis.xaris.unipiplialert.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.xoulis.xaris.unipiplialert.data.EventsContract.EventsEntry;

public class EventsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "waitlist.db";

    private static final int DATABASE_VERSION = 1;

    public EventsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " + EventsEntry.TABLE_NAME + " (" +
                EventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EventsEntry.COLUMN_TYPE_OF_EVENT + " TEXT NOT NULL, " +
                EventsEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
