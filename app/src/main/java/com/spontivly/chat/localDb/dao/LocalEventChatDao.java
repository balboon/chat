package com.spontivly.chat.localDb.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.spontivly.chat.localDb.entity.LocalEventChat;

import java.util.Date;
import java.util.List;

@Dao
public interface LocalEventChatDao {
    @Query("SELECT * FROM LocalEventChat WHERE eventId = :id")
    List<LocalEventChat> getEventChatMessages(int id);

    @Query("SELECT * FROM LocalEventChat WHERE eventId = :id AND createdAt > :lastMessageAt")
    List<LocalEventChat> getEventChatMessages(int id, Date lastMessageAt);

    @Insert
    void postMessage(LocalEventChat msg);
}
