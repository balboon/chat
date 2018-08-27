package com.spontivly.chat.localDb.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.spontivly.chat.localDb.entity.LocalUsers;

@Dao
public interface LocalUsersDao {
    @Query("SELECT * FROM LocalUsers WHERE userId = :id")
    LocalUsers getLocalUser(int id);

    @Insert
    void addLocalUser(LocalUsers user);
}
