package com.spontivly.chat.localDb.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class LocalUsers {

    @PrimaryKey
    @NonNull
    public int userId;

    public String firstName;
    public String lastName;
}
