package com.spontivly.chat.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the chat database. This class is not necessary, but keeps
 * the code organized.
 */
public class ChatContract {
    public static final String CONTENT_AUTHORITY = "com.spontivly.app";
    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Spontivly.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT_CHAT = "eventChat";

    /* Inner class that defines the table contents of the weather table */
    public static final class EventChatEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_CHAT)
                .build();

        /* Used internally as the name of our event chat table. */
        public static final String TABLE_NAME = "EventChatTable";

        /*
         * The timestamp column will store the UTC date that correlates to the local date.
         *
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */
        public static final String COLUMN_TIMESTAMP = "timestamp";


        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_SENDER_ID = "sender_id";
        public static final String COLUMN_MESSAGE = "message";
    }

}
