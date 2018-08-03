package com.spontivly.chat;

public class EventChatItem {
    private int mImageResource;
    private String mEvent;
    private String mMembers;

    public EventChatItem(int imageResource, String event, String members) {
        mImageResource = imageResource;
        mEvent = event;
        mMembers = members;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getEventName() {
        return mEvent;
    }

    public String getMembers (){
        return mMembers;
    }
}
