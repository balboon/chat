package com.spontivly.chat.models;

import java.util.Map;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Users {
    public String user_name;
    public Map<String,Integer> events;

    public Users(){

    }

    public Users(String user_name, Map<String, Integer> events) {
        this.user_name = user_name;
        this.events = events;
    }

    public String getUser_name(){
        return this.user_name;
    }

    public Map<String,Integer> getEvents(){
        return this.events;
    }

    @Override
    public String toString(){
        String return_string = getUser_name() + "\n";
        for (Map.Entry<String,Integer> entry : events.entrySet()) {
            return_string += entry.getKey() + ": " + entry.getValue() +"\n";
        }
        return return_string;
    }
}
