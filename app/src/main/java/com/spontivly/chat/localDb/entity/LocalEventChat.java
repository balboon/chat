package com.spontivly.chat.localDb.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class LocalEventChat {

    @PrimaryKey
    @NonNull
    public int eventMessageId;

    public int eventId;
    public int posterId;
    public String postedMessage;
    public String posterFirstName;
    public String posterLastName;
    public Date createdAt;
}
