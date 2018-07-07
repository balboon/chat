package com.spontivly.chat.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class SpontivlyEvent {

    public ArrayList<SpontivlyUser> joinedUsers = new ArrayList<>();

    public boolean isNew;
    public int eventId;
    public int typeId;
    //int ownerId;
    LatLng pos;
    public String title;
    public String desc;
    public String address;

    public long startTime;
    public long endTime;
    public boolean hasOwnerArrived;
    public int maxUsers;
    public SpontivlyUser owner;
    public boolean showOwnerPhone;
    public boolean isPrivate;

    public SpontivlyEvent(){}

    public SpontivlyEvent(boolean isNew){
        this.isNew = isNew;
    }

    public int getOwnerId(){
        return owner == null? 0 : owner.userId;
    }

    public void setPos(LatLng pos) {
        this.pos = pos;
    }

    public LatLng getPos() {
        return pos;
    }

    public boolean hasJoinedUser(int userId) {
        for(int i = joinedUsers.size(); i-->0;){
            if(joinedUsers.get(i).userId == userId){ return true; }
        }
        return false;
    }

    public void removeJoinedUser(int userId) {
        for(int i = joinedUsers.size(); i-->0;){
            if(joinedUsers.get(i).userId == userId){ joinedUsers.remove(i); }
        }
    }

    public boolean isFullOfJoinedUsers() {
        return joinedUsers.size() + 1 >= maxUsers && maxUsers >= 2;
    }
}