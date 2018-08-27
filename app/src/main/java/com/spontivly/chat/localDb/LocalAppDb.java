package com.spontivly.chat.localDb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.spontivly.chat.localDb.dao.LocalEventChatDao;
import com.spontivly.chat.localDb.dao.LocalEventsDao;
import com.spontivly.chat.localDb.dao.LocalUsersDao;
import com.spontivly.chat.localDb.entity.LocalEventChat;
import com.spontivly.chat.localDb.entity.LocalEvents;
import com.spontivly.chat.localDb.entity.LocalUsers;

@Database(entities = {LocalEvents.class, LocalEventChat.class, LocalUsers.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class LocalAppDb extends RoomDatabase {

    private static LocalAppDb sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "spontivly-db";

    public abstract LocalEventsDao localEventDao();
    public abstract LocalEventChatDao localEventChatDao();
    public abstract LocalUsersDao localUsersDao();

    public static LocalAppDb getDatabase(Context context) {
        if (sInstance == null) {
            synchronized (LocalAppDb.class) {
                if (sInstance == null) {
                    sInstance =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    LocalAppDb.class,
                                    DATABASE_NAME)
                                    .build();
                }
            }
        }
        return sInstance;
    }

    public static void destroyInstance() {
        sInstance = null;
    }

}
