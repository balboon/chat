package com.spontivly.chat;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.spontivly.chat.localDb.LocalAppDb;
import com.spontivly.chat.localDb.LocalDbUtils;
import com.spontivly.chat.localDb.dao.LocalEventChatDao;
import com.spontivly.chat.localDb.dao.LocalEventsDao;
import com.spontivly.chat.localDb.dao.LocalUsersDao;
import com.spontivly.chat.localDb.entity.LocalEventChat;
import com.spontivly.chat.localDb.entity.LocalEvents;
import com.spontivly.chat.localDb.entity.LocalUsers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TestLocalDB {
    private LocalEventsDao localEventsDao;
    private LocalEventChatDao localEventChatDao;
    private LocalUsersDao localUsersDao;
    private LocalAppDb mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, LocalAppDb.class).build();
        localEventChatDao = mDb.localEventChatDao();
        localEventsDao = mDb.localEventDao();
        localUsersDao = mDb.localUsersDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void writeEventChatAndRead() throws Exception {
        LocalEventChat chat = new LocalEventChat();
        chat.posterId = 1;
        chat.eventId = 1;
        chat.postedMessage = "Hello there";
        chat.posterFirstName = "Al";
        chat.posterLastName = "Ma";
        chat.createdAt = new Date();

        localEventChatDao.postMessage(chat);
        List<LocalEventChat> msgs = localEventChatDao.getEventChatMessages(1);
        boolean match = LocalDbUtils.compareEventchat(chat, msgs.get(0));
        assertThat("Match", match);
    }

    @Test
    public void writeEventAndRead() throws Exception {
        LocalEvents event = new LocalEvents();

        event.eventId = 1;
        event.typeId = 1;
        event.ownerId = 1;
        event.showOwnerPhone = true;
        event.title = "Test Event";
        event.description = "Big party for everyone!";
        event.address = "Mercer Tavern";
        event.lat = 123456;
        event.lng = 456789;
        event.roughLat = 12345;
        event.roughLng = 54321;
        event.startTime = new Date();
        event.endTime = new Date();
        event.hasOwnerArrived = true;
        event.maxUsers = 10;
        event.ispublic = true;
        event.isVisible = true;;
        event.isArchived = true;;
        event.joined = true;;

        localEventsDao.addOrUpdateEvent(event);
        LocalEvents events = localEventsDao.getEvent(1);
        boolean match = LocalDbUtils.compareEvents(event, events);
        assertThat("Match", match);
    }

    @Test
    public void writeUserAndRead() {
        LocalUsers user = new LocalUsers();
        user.firstName = "Bobby";
        user.lastName = "Logan";
        user.userId = 1;

        localUsersDao.addLocalUser(user);
        LocalUsers newUser = localUsersDao.getLocalUser(1);
        boolean match = LocalDbUtils.compareUsers(user, newUser);
        assertThat("Match", match);
    }
}
