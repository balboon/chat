package com.spontivly.chat.localDb.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class LocalEvents {

    @PrimaryKey
    @NonNull
    public int eventId;

    public int typeId;
    public int ownerId;
    public boolean showOwnerPhone;
    public String title;
    public String description;
    public String address;
    public double lat;
    public double lng;
    public double roughLat;
    public double roughLng;
    public Date startTime;
    public Date endTime;
    public boolean hasOwnerArrived;
    public int maxUsers;
    public boolean ispublic;
    public boolean isVisible;
    public boolean isArchived;
    public boolean joined;

}
