package com.spontivly.chat.models;

import java.util.ArrayList;

public class SpontivlyEventChat {

    public ArrayList<SpontivlyEventChatMessage> chatMessages = new ArrayList<>();

    public SpontivlyEventChatMessage lastPostedMessage;
    public int eventId;

    public SpontivlyEventChat(){}

    public void postMessage(SpontivlyEventChatMessage newMessage) {
        // Add message to arraylist and update lastPostedMessage
        chatMessages.add(newMessage);
        this.lastPostedMessage = newMessage;
    }
}
