package com.spontivly.chat.data;

public class AppConstants {

    //Used to set range for TravelSettingsSlider
    public static final int MIN_TRAVEL = 1000;
    public static final int MAX_TRAVEL = 5 * 1000;

    //Shortest event
    public static int MIN_EVENT_DURATION = 1 * 60 * 60;

    //Longest event
    public static int MAX_EVENT_DURATION = 12 * 60 * 60;

    //Time into the future, that we'll allow you to create an event.
    public static int MAX_EVENT_DELAY = 12 * 60 * 60;

    //Distance to pin, required before auto-checkin fires.
    public static int PIN_CHECKIN_DISTANCE = 150;

}
