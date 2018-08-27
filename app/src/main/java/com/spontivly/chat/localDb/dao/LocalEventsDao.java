package com.spontivly.chat.localDb.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.spontivly.chat.localDb.entity.LocalEvents;

import java.util.List;

@Dao
public interface LocalEventsDao {
    @Query("SELECT * FROM LocalEvents")
    List<LocalEvents> getAll();

    @Query("SELECT * FROM LocalEvents WHERE eventId = :id")
    LocalEvents getEvent(int id);

    @Query("SELECT * FROM LocalEvents WHERE isArchived = 0")
    List<LocalEvents> getActiveEvents();

    @Insert
    void addOrUpdateEvent(LocalEvents events);
}
