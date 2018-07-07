package com.spontivly.chat.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import com.spontivly.chat.data.CommonLocations;
import com.spontivly.chat.utils.AppUtils;

public class AppModel {

    public class AppData {

        //Save a list of all users that we've seen for a given event, so we don't send a notification twice about the same user.
        public HashMap<Integer, ArrayList<Integer>> userIdsByEventId;

        public ArrayList<Integer> disabledActivities;

        public ArrayList<SpontivlyEvent> currentEvents;
        public SpontivlyUser currentUser;
        public boolean disableBackgroundService = false;
        public int maxTravel = 3000;
        public LatLng lastKnownPos;
        public boolean enableDevMode;

        public void verifyData(){
            if(userIdsByEventId == null){ userIdsByEventId = new HashMap<>();}
            if(disabledActivities == null){ disabledActivities = new ArrayList<>(); }
            if(maxTravel == 0){ maxTravel = 3000;}
        }
    }


    private final Context context;


    public boolean isDevMode() { return appData.enableDevMode; }

    public interface OwnedEventChanged { public void OnAppModelOwnedEventChanged(SpontivlyEvent event); }
    public OwnedEventChanged ownedEventChangedCallback;

    public interface UserChangedCallback { public void OnAppModelUserChanged(SpontivlyUser user); }
    public UserChangedCallback userChangedCallback;

    public interface UserJoinedEventCallback { public void OnAppModelUserJoined(SpontivlyUser user); }
    public UserJoinedEventCallback userJoinedEventCallback;

    public AppData appData;
    public SpontivlyEvent currentOwnedEvent;
    public SpontivlyEvent currentJoinedEvent;

    public SpontivlyEvent getOwnedEvent(){ return currentOwnedEvent; }

    public void setJoinedEvent(SpontivlyEvent e){
        currentOwnedEvent = e;
    }

    public void setOwnedEvent(SpontivlyEvent e){
        currentOwnedEvent = e;
        if(currentOwnedEvent != null){
            //Create an entry in the hashMap for this event, we'll save the userId's that we know about in here. And when we see a new one, we'll send an alert.
            if(!appData.userIdsByEventId.containsKey(e.eventId)){
                appData.userIdsByEventId.put(e.eventId, new ArrayList<Integer>());
            }
            //All the userId's we currently know about for this eventId
            ArrayList<Integer> knownUserIdList = appData.userIdsByEventId.get(e.eventId);
            //Loop through users in event, if we've not seen them before, send an alert, but just one.
            boolean hasFoundNewUser = false;
            for(int i = e.joinedUsers.size(); i-->0;){
                SpontivlyUser user = e.joinedUsers.get(i);
                if(knownUserIdList.indexOf(user.userId) == -1){
                    //New user!
                    if(!hasFoundNewUser && userJoinedEventCallback != null){
                        userJoinedEventCallback.OnAppModelUserJoined(e.joinedUsers.get(i));
                    }
                    hasFoundNewUser = true;
                    knownUserIdList.add(user.userId);
                }
            }
            if(hasFoundNewUser){
                saveAppData();
            }
        }
        if(ownedEventChangedCallback != null){
            ownedEventChangedCallback.OnAppModelOwnedEventChanged(currentOwnedEvent);
        }
    }

    public AppModel(Context context, boolean autoLoad){
        this.context = context;
        if(autoLoad){
            loadAppData();
        }
        if(appData == null){ appData = new AppData(); }
        appData.verifyData();
//        refreshEventTypes();
    }

    public void loadAppData() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString("AppData", null);
        if(json == null){
            appData = new AppData();
            saveAppData();
        } else {
            Gson gson = new Gson();
            try {
                appData = gson.fromJson(json, AppData.class);
            }
            //Data was malformed somehow
            catch (Exception e){
                appData = new AppData();
                saveAppData();
                AppUtils.alert("There was an error loading your saved data.", null);
            }
        }
    }

    public void saveAppData(){
        Gson gson = new Gson();
        String json = gson.toJson(appData);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("AppData", json).apply();
    }

    /////////////////////////////////////////////////////////////////////
    // PUBLIC API
    public boolean isValidUserSignedIn(){ return  appData.currentUser != null && appData.currentUser.isGuest() == false; }

    public SpontivlyUser getUser(){
        return appData.currentUser;
    }

    public void setUser(SpontivlyUser value) {
        appData.currentUser = value;
        saveAppData();
        if(userChangedCallback != null){
            userChangedCallback.OnAppModelUserChanged(getUser());
        }
    }

    public int getUserId() {
        return getUser() != null? getUser().userId : -1;
    }

    public boolean isEventTypeDisabled(int typeId) {
        if(appData == null || appData.disabledActivities == null){return false;}
        return appData.disabledActivities.indexOf(typeId) != -1;
    }

    public boolean isOurEvent(SpontivlyEvent e) {
        if(appData == null || appData.currentUser == null || e == null){return false;}
        return e.getOwnerId() == appData.currentUser.userId;
    }

    public LatLng getLastKnownPos() {
        if(appData == null){return CommonLocations.NORTH_AMERICA;}
        return appData.lastKnownPos;
    }

    public void setLastKnownPos(LatLng pos) {
        if(appData == null){return;}
        appData.lastKnownPos = pos;
    }

    /////////////////////////////////////////////////////////////////////
    // EVENT TYPES

    //This simulates the data downloaded from the database, all UI should be driven by this array.
    public ArrayList<SpontivlyEventType> eventTypes = new ArrayList<>();

//    public void refreshEventTypes(){
//        eventTypes.clear();
//
//        //TODO: Replace with database download? Still would require hardcoded list for fallback... ?
//        eventTypes.add(new SpontivlyEventType(0, "Choose Type...", R.drawable.type_icon_misc));
//        eventTypes.add(new SpontivlyEventType(1, "Hockey", R.drawable.type_icon_hockey));
//        eventTypes.add(new SpontivlyEventType(2, "Basketball", R.drawable.type_icon_basketball));
//        eventTypes.add(new SpontivlyEventType(3, "Soccer", R.drawable.type_icon_soccer));
//        eventTypes.add(new SpontivlyEventType(5, "Baseball", R.drawable.type_icon_baseball));
//        eventTypes.add(new SpontivlyEventType(6, "Football", R.drawable.type_icon_football));
//        eventTypes.add(new SpontivlyEventType(9, "Beauty and Fashion", R.drawable.type_icon_fashion));
//        eventTypes.add(new SpontivlyEventType(8, "Education and Study Groups", R.drawable.type_icon_education));
//        eventTypes.add(new SpontivlyEventType(10, "Food and Drink", R.drawable.type_icon_food));
//        eventTypes.add(new SpontivlyEventType(11, "Gaming", R.drawable.type_icon_gaming));
//        eventTypes.add(new SpontivlyEventType(12, "Gym and Fitness", R.drawable.type_icon_gym));
//        eventTypes.add(new SpontivlyEventType(13, "Music", R.drawable.type_icon_music));
//        eventTypes.add(new SpontivlyEventType(14, "Nature and Outdoors", R.drawable.type_icon_nature));
//        eventTypes.add(new SpontivlyEventType(15, "Networking", R.drawable.type_icon_networking));
//        eventTypes.add(new SpontivlyEventType(16, "Nightlife", R.drawable.type_icon_nightlife));
//        eventTypes.add(new SpontivlyEventType(22, "Outdoor Cardio", R.drawable.type_icon_soccer));
//        eventTypes.add(new SpontivlyEventType(17, "Pets and Animals", R.drawable.type_icon_pets));
//        eventTypes.add(new SpontivlyEventType(18, "Photography and Film", R.drawable.type_icon_photography));
//        eventTypes.add(new SpontivlyEventType(19, "Popup Shop", R.drawable.type_icon_popupshop));
//        //eventTypes.add(new SpontivlyEventType(20, "Soccer", R.drawable.type_icon_soccer));
//        eventTypes.add(new SpontivlyEventType(21, "Volunteer", R.drawable.type_icon_volunteer));
//        eventTypes.add(new SpontivlyEventType(7, "Misc", R.drawable.type_icon_misc));
//
//    }

    public SpontivlyEventType getEventType(int typeId) {
        for(int i = eventTypes.size(); i-->0;){
            if(eventTypes.get(i).typeId == typeId){ return eventTypes.get(i); }
        }
        return null;
    }

    public SpontivlyEventType getEventType(String name) {
        for(int i = eventTypes.size(); i-->0;){
            if(eventTypes.get(i).name == name){ return eventTypes.get(i); }
        }
        return null;
    }

}
