package com.spontivly.chat;

public class EventChatItem {
    private int mImageResource;
    private String mEvent;
    private String mLastMsg;
    private int mEventId;

    public EventChatItem(int imageResource, int eventId, String event, String lastMsg) {
        mImageResource = imageResource;
        mEvent = event;
        mLastMsg = lastMsg;
        mEventId = eventId;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getEventName() {
        return mEvent;
    }

    public String getLastMessage(){
        return mLastMsg;
    }

    public int getEventId() { return mEventId; }
}
