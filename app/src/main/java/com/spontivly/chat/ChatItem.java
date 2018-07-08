package com.spontivly.chat;

public class ChatItem {
    private int mImageResource;
    private String mEvent;
    private String mMembers;

    public ChatItem(int imageResource, String event, String members) {
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
