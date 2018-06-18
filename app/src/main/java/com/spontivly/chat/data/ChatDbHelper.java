package com.spontivly.chat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.spontivly.chat.data.ChatContract.EventChatEntry;

/**
 * Manages a local database for event chat data.
 */
public class ChatDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "spontivly_test.db";

    private static final int DATABASE_VERSION = 1;

    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + EventChatEntry.TABLE_NAME + " (" +

                        EventChatEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EventChatEntry.COLUMN_EVENT_ID   + " INTEGER NOT NULL, "                 +
                        EventChatEntry.COLUMN_SENDER_ID +  " INTEGER NOT NULL,"                  +
                        EventChatEntry.COLUMN_MESSAGE   +  " TEXT NOT NULL, "                    +
                        EventChatEntry.COLUMN_TIMESTAMP +  " INTEGER NOT NULL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventChatEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
