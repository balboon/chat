package com.spontivly.chat.models;

public class SpontivlyEventChatMessage {

    public int eventId;
    public int posterId;
    public String posterFirstName;
    public String posterLastName;
    public String postedMessage;
    public long createdAt;

    public SpontivlyEventChatMessage(){
    }

    @Override
    public String toString() {
        return "EventId:" + this.eventId +
                ", PosterId:" + this.posterId + ", " + this.posterFirstName + " " + this.posterLastName +
                ", Msg:" + this.postedMessage +
                ", Created at:" + createdAt;
    }
}
