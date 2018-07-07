package com.spontivly.chat.models;

public class SpontivlyEventType {

    public int typeId;
    public String name;
    public int icon;

    public SpontivlyEventType(int id, String name, int iconResourceId){
        this.typeId = id;
        this.name = name;
        this.icon = iconResourceId;
    }

    public SpontivlyEventType(int id, String name){
        this.typeId = id;
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
