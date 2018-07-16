package com.spontivly.chat;

public class MyMessageItem {
    private String mMsg;
    private long mTime;

    public MyMessageItem(String msg, long time) {
        mMsg = msg;
        mTime = time;
    }


    public String getMsg() {
        return mMsg;
    }

    public long getTime() {
        return mTime;
    }
}
