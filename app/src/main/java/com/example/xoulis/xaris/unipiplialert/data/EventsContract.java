package com.example.xoulis.xaris.unipiplialert.data;

import android.provider.BaseColumns;

class EventsContract {
    class EventsEntry implements BaseColumns {
        static final String TABLE_NAME = "events";
        static final String COLUMN_TYPE_OF_EVENT = "type_of_event";
        static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
