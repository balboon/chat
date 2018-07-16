package com.spontivly.chat;

public class MessageItem {
    private String mUser;
    private String mMsg;
    private long mTime;
    private int mID;

    public MessageItem(int id, String user, String msg, long time) {
        mUser = user;
        mMsg = msg;
        mTime = time;
        mID = id;
    }

    public int getID() { return mID; }

    public String getUser() { return mUser; }

    public String getMsg() {
        return mMsg;
    }

    public long getTime() {
        return mTime;
    }
}
