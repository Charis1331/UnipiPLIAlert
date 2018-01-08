package com.example.xoulis.xaris.unipiplialert.data;

import android.provider.BaseColumns;

public class EventsContract {
    public class EventsEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_TYPE_OF_EVENT = "type_of_event";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
