package com.spontivly.chat.services;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.spontivly.chat.models.AppModel;
import com.spontivly.chat.data.AppConstants;
import com.spontivly.chat.models.SpontivlyEvent;
import com.spontivly.chat.models.SpontivlyEventChat;
import com.spontivly.chat.models.SpontivlyEventChatMessage;
import com.spontivly.chat.models.SpontivlyUser;
import com.spontivly.chat.utils.AppUtils;
import com.spontivly.chat.utils.TextUtils;

public class DatabaseService {

    public ArrayList<Integer> ignoredEventTypes = new ArrayList<>();

    public interface JoinEventCallback { abstract void callback(boolean success); }
    public interface UpdateUserCallback { abstract void callback(int userId); }
    public interface UpdateCallback { abstract void callback(int eventId); }
    public interface DeleteCallback { abstract void callback(boolean response); }
    public interface GetUsersCallback { abstract void callback(ArrayList<SpontivlyUser> response); }
    public interface GetActiveCallback { abstract void callback(ArrayList<SpontivlyEvent> response); }
    public interface UpdateEventChatCallback { abstract void callback(int messageId); }
    public interface GetEventChatCallback { abstract void callback(SpontivlyEventChat eventChat); }
    public interface GetEventChatMessagesCallback { abstract void callback(ArrayList<SpontivlyEventChatMessage> response); }
    public interface GetUserInfoCallback { abstract void callback(SpontivlyUser user); }

    public String baseApiUrl = "http://spontivly.com/db/api.php?";
    public String chatBaseApiUrl = "http://spontivly.com/chat/api.php?";

    public AppModel model;
    public RequestQueue netRequests;

    public DatabaseService(){
    }

    ///////////////////////////////////////////////////////////////////////
    // ADD/UPDATE USER DATA
    ///////////////////////////////////////////////////////////////////////
    public void addOrUpdateUser(SpontivlyUser user, final UpdateUserCallback callback) {
        //Build query url
        String url = baseApiUrl + "api=registerUser";
        url = addParam(url, "fbUserId", user.fbUserId);
        url = addParam(url, "googleId", user.googleId);
        url = addParam(url, "linkedInId", user.linkedInId);
        url = addParam(url, "instagramId", user.instagramId);
        url = addParam(url, "profileUri", user.profileUri);
        url = addParam(url, "profilePicUri", user.profilePicUri);
        url = addParam(url, "firstName", user.firstName);
        url = addParam(url, "lastName", user.lastName);
        url = addParam(url, "birthdate", user.birthdate);
        url = addParam(url, "gender", user.gender);
        url = addParam(url, "email", user.email);
        url = addParam(url, "phoneNumber", user.phoneNumber);
        if(model.isDevMode()){
            Log.e("addOrUpdateUser", url);
        }
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                public void onResponse(String response) {
                //AppUtils.toast("Delete Completed " + response);
                int responseId = -1;
                if(callback != null){
                    try {
                        responseId = Integer.parseInt(response);
                    } catch (Exception e){
                        Log.e("Spontivly", Log.getStackTraceString(e));
                    };
                    callback.callback(responseId);
                }
                }
            },
            new Response.ErrorListener(){
                public void onErrorResponse(VolleyError error){
                AppUtils.toast("registerFbUser Error " + error);
                if(callback != null){
                    callback.callback(-1);
                }
                }
            });
        netRequests.add(jsObjRequest);
    }


    ///////////////////////////////////////////////////////////////////////
    // ADD/UPDATE EVENT
    ///////////////////////////////////////////////////////////////////////
    public void addOrUpdateEvent(SpontivlyEvent event, final UpdateCallback callback) {

        String url = baseApiUrl + "api=" + (event.isNew? "addEvent" : "updateEvent");
        url = addParam(url, "eventId", event.eventId);
        url = addParam(url, "typeId", event.typeId);
        url = addParam(url, "ownerId", event.getOwnerId());
        url = addParam(url, "title", event.title);
        url = addParam(url, "desc", event.desc);
        url = addParam(url, "address", event.address);
        url = addParam(url, "startTime", event.startTime);
        url = addParam(url, "endTime", event.endTime);
        url = addParam(url, "showOwnerPhone", event.showOwnerPhone);
        url = addParam(url, "hasOwnerArrived", event.hasOwnerArrived);
        url = addParam(url, "maxUsers", event.maxUsers);
        url = addParam(url, "isPrivate", event.isPrivate);
        url = addParam(url, "lat", event.getPos().latitude);
        url = addParam(url, "lng", event.getPos().longitude);

        if(model.isDevMode()){
            Log.e("addOrUpdateEvent", url);
        }
        //AppUtils.Toast("Starting update: " + url);
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        int responseId = -1;
                        if(callback != null){
                            try {
                                responseId = Integer.parseInt(response);
                            } catch (Exception e){
                                Log.e("Spontivly", Log.getStackTraceString(e));
                            };
                            callback.callback(responseId);
                        }
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        if(callback != null){
                            callback.callback(-1);
                        }
                        AppUtils.toast("Update Error " + error);
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // LEAVE/JOIN EVENTS
    ///////////////////////////////////////////////////////////////////////
    public void updateUserAtEvent(boolean isLeaving, int userId, int eventId, final JoinEventCallback callback) {
        String url = baseApiUrl + "api=updateUserAtEvent";
        url = addParam(url, "userId", userId);
        url = addParam(url, "eventId", eventId);
        url = addParam(url, "isLeaving", isLeaving);

        StringRequest jsObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        AppUtils.toast("updateUserAtEvent Completed " + response);
                        if(callback != null){ callback.callback(true); }
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        AppUtils.toast("updateUserAtEvent Error " + error);
                        if(callback != null){ callback.callback(false); }
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // GET USERS AT EVENT
    public void getUsersAtEvent(String eventId, final GetUsersCallback callback) {
        String url = baseApiUrl + "api=getUsersAtEvents";
        url = addParam(url, "eventIdList", eventId);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        ArrayList<SpontivlyUser> users = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonobject = response.getJSONObject(i);
                                users.add(parseUserFromJson(jsonobject, "userId"));
                            } catch (Exception e){}
                        }
                        if(callback != null){
                            callback.callback(users);
                        }
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        callback.callback(new ArrayList<SpontivlyUser>());
                        Log.e("Spontivly", Log.getStackTraceString(error));
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // DELETE EVENT
    ///////////////////////////////////////////////////////////////////////
    public void deleteEvent(int id, final DeleteCallback callback) {
        String url = baseApiUrl + "api=deleteEvent";
        url = addParam(url, "eventId", id);

        if(model.isDevMode()){
            Log.d("deleteEvent", url);
        }

        StringRequest jsObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        AppUtils.toast("Delete Completed " + response);
                        if(callback != null){
                            callback.callback(true);
                        }
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        AppUtils.toast("Delete Error " + error);
                        if(callback != null){
                            callback.callback(false);
                        }
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // FETCH ACTIVE EVENTS
    ///////////////////////////////////////////////////////////////////////
    public void getActiveEvents(LatLng pos, final GetActiveCallback callback){

        String url = baseApiUrl;
        url = addParam(url, "lat", pos.latitude);
        url = addParam(url, "lng", pos.longitude);
        if(model.isDevMode()){
            Log.e("getActiveEvents", url);
        }
        //AppUtils.Toast("get " + url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        //Parse events
                        final ArrayList<SpontivlyEvent> events = parseActiveEvents(response);

                        //Stuff a hashMap full of events for quick lookup
                        final HashMap<Integer, SpontivlyEvent> eventsById = new HashMap<>();
                        for(int i = events.size(); i-->0;){ eventsById.put(events.get(i).eventId, events.get(i)); }

                        //Build a comma-delimted list of eventIds, so we can do the query in one-shot
                        ArrayList<Integer> eventIds = new ArrayList<>();
                        for(int i = 0; i < events.size(); i++){ eventIds.add(events.get(i).eventId); }

                        //Fetch attending users
                        String url2 = baseApiUrl + "api=getUsersAtEvents&eventIdList=" + TextUtils.join(",", eventIds);
                        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url2, null,
                                new Response.Listener<JSONArray>() {
                                    public void onResponse(JSONArray response) {
                                        //User have been fetched, loop through each one, and inject it into the matching event
                                        for (int i = 0; i < response.length(); i++) {
                                            try {
                                                JSONObject jso = response.getJSONObject(i);
                                                int eventId = jso.getInt("eventId");
                                                if(eventsById.containsKey(eventId)){
                                                    //Inject user into event
                                                    SpontivlyUser user = parseUserFromJson(jso, "userId");
                                                    eventsById.get(eventId).joinedUsers.add(user);
                                                    Log.e("Spontivly", "JoinedUser = " + user.firstName);
                                                }
                                            } catch (Exception e){
                                                Log.e("Spontivly", Log.getStackTraceString(e));
                                            }
                                        }
                                        //All events are done, and users are injected. We're done!
                                        if(callback != null){callback.callback(events); }
                                    }
                                },
                                new Response.ErrorListener(){
                                    public void onErrorResponse(VolleyError error){
                                        callback.callback(events);
                                        Log.e("Spontivly", Log.getStackTraceString(error));
                                    }
                                });
                        netRequests.add(jsObjRequest);
                        //if(callback != null){ callback.callback(events); }
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        Log.e("Spontivly", Log.getStackTraceString(error));
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // POST/UPDATE EVENT CHAT
    ///////////////////////////////////////////////////////////////////////
    public void postEventChatMessage(SpontivlyEventChatMessage msg, final UpdateEventChatCallback callback) {
        //Build query url
        String url = chatBaseApiUrl + "api=postEventMessage";
        url = addParam(url, "eventId", msg.eventId);
        url = addParam(url, "userId", msg.posterId);
        url = addParam(url, "message", msg.postedMessage);
        url = addParam(url, "createdAt", msg.createdAt);
//        if(model.isDevMode()){
            Log.e("postEventMessage", url);
//        }
        StringRequest jsObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        //AppUtils.toast("Delete Completed " + response);
                        int responseId = -1;
                        if(callback != null){
                            try {
                                responseId = Integer.parseInt(response);
                            } catch (Exception e){
                                Log.e("Spontivly", Log.getStackTraceString(e));
                            };
                            callback.callback(responseId);
                        }
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        AppUtils.toast("postEventMessage Error " + error);
                        if(callback != null){
                            callback.callback(-1);
                        }
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // FETCH EVENT CHAT MESSAGES
    ///////////////////////////////////////////////////////////////////////
    public void getEventChatMessages(int eventId, long lastMessageAt, final GetEventChatMessagesCallback callback){

        String url = chatBaseApiUrl + "api=getEventMessages";;
        url = addParam(url, "eventId", eventId);
        url = addParam(url, "lastMessageAt", lastMessageAt);

//        if(model.isDevMode()){
            Log.e("getEventChatMessages", url);
//        }
        //AppUtils.Toast("get " + url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        //Parse event chat messages
                        final ArrayList<SpontivlyEventChatMessage> eventChatMessages = parseEventChatMessages(response);

                        if(callback != null){callback.callback(eventChatMessages);}
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        Log.e("Spontivly", Log.getStackTraceString(error));
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // FETCH EVENT CHAT
    ///////////////////////////////////////////////////////////////////////
    public void getEventChat(final int eventId, final GetEventChatCallback callback){

        String url = chatBaseApiUrl + "api=getEventMessages";;
        url = addParam(url, "eventId", eventId);

//        if(model.isDevMode()){
        Log.e("getEventChat", url);
//        }
        //AppUtils.Toast("get " + url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        //Parse event chat messages
                        final ArrayList<SpontivlyEventChatMessage> eventChatMessages = parseEventChatMessages(response);
                        SpontivlyEventChat chat = new SpontivlyEventChat();
                        chat.chatMessages = eventChatMessages;
                        if (!eventChatMessages.isEmpty())
                            chat.lastPostedMessage = eventChatMessages.get(eventChatMessages.size() - 1);
                        else
                            chat.lastPostedMessage = null;
                        chat.eventId = eventId;

                        callback.callback(chat);
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        Log.e("Spontivly", Log.getStackTraceString(error));
                    }
                });
        netRequests.add(jsObjRequest);
    }

    ///////////////////////////////////////////////////////////////////////
    // FETCH USER INFO
    ///////////////////////////////////////////////////////////////////////
    public void getUserInfo(final int userId, final GetUserInfoCallback callback){

        String url = chatBaseApiUrl + "api=getUserInfo";;
        url = addParam(url, "userId", userId);

//        if(model.isDevMode()){
        Log.e("getUserInfo", url);
//        }
        //AppUtils.Toast("get " + url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        SpontivlyUser user = new SpontivlyUser();
                        user.userId = userId;
                        if (response.length() > 0) {
                            try {
                                user = parseUserFromJson(response.getJSONObject(0), "userId");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        callback.callback(user);
                    }
                },
                new Response.ErrorListener(){
                    public void onErrorResponse(VolleyError error){
                        Log.e("Spontivly", Log.getStackTraceString(error));
                    }
                });
        netRequests.add(jsObjRequest);
    }

    /////////////////////////////////////////////////
    // PARSE JSON
    /////////////////////////////////////////////////
    private ArrayList<SpontivlyEvent> parseActiveEvents(JSONArray response) {
        ArrayList<SpontivlyEvent> events = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonobject = response.getJSONObject(i);
                    //Create event data object
                    SpontivlyEvent e = new SpontivlyEvent();
                    e.eventId = jsonobject.getInt("eventId");
                    e.typeId = jsonobject.getInt("typeId");
                    e.title = dec(jsonobject.getString("title"));
                    e.desc = dec(jsonobject.getString("description"));
                    e.address = dec(jsonobject.getString("address"));
                    e.startTime = jsonobject.getLong("startTime");
                    if(e.startTime == -1){
                        e.startTime = (long) (System.currentTimeMillis() * .001) - (60 * 15);
                    }
                    e.endTime = jsonobject.getLong("endTime");
                    if(e.endTime == -1){
                        e.endTime = (long) (System.currentTimeMillis() * .001) + (60 * 60);
                    }
                    e.hasOwnerArrived = jsonobject.getInt("hasOwnerArrived") == 1;
                    e.maxUsers = jsonobject.getInt("maxUsers");
                    e.isPrivate = jsonobject.getInt("isPrivate") == 1;
                    e.showOwnerPhone = jsonobject.getInt("showOwnerPhone") == 1;
                    e.setPos(new LatLng(jsonobject.getDouble("lat"), jsonobject.getDouble("lng")));

                    //Create owner data object
                    SpontivlyUser owner = new SpontivlyUser();
                    owner.userId = jsonobject.getInt("ownerId");
                    e.owner = parseUserFromJson(jsonobject, "ownerId");

                    //Do a final check to see whether we want to discard/delete this event
                    boolean addToList = true;
                    boolean isOurs = model.isOurEvent(e);

                    //Is it old?
                    if(e.endTime >= 0 && e.endTime < System.currentTimeMillis() * .001){
                        //Whether it's ours or not, we don't want to see it
                        addToList = false;
                        //If it's ours, delete it from db
                        if(isOurs){
                            deleteEvent(e.eventId, null);
                        }
                    }
                    //Do we own it and have not arrived? Check the time, and maybe delete it
                    if(isOurs && !e.hasOwnerArrived) {
                        double secondsSinceStart = (System.currentTimeMillis() * .001) - e.startTime;
                        double maxSeconds = AppConstants.MAX_EVENT_DELAY    ;//Check whether it's exceeded the max delay...
                        //maxSeconds = 60 * 3;
                        //Time's up!
                        if (secondsSinceStart > maxSeconds) {
                            deleteEvent(e.eventId, null);
                            addToList = false;
                            AppUtils.alert("Activity Deleted", "You didn't arrive at your pin on time, so it was automatically deleted.");
                        }
                    }
                    //Is it not ours, and the type is being ignored? SKIP IT
                    if(!isOurs && model.isEventTypeDisabled(e.typeId)){
                        addToList = false;
                    }
                    //Add the event to the list if the flag is still true
                    if(addToList){
                        events.add(e);
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        } catch (Exception e){ Log.e("Spontivly", Log.getStackTraceString(e)); }

        return events;
    }

    private SpontivlyUser parseUserFromJson(JSONObject jsonobject, String idField) {
        SpontivlyUser user = new SpontivlyUser();
        try {
            user.userId = jsonobject.getInt(idField);
            //Social Ids
            user.fbUserId = jsonobject.getString("fbUserId");
            user.googleId = jsonobject.getString("googleId");
            user.instagramId = jsonobject.getString("instagramId");
            user.linkedInId = jsonobject.getString("linkedInId");
            user.profileUri = dec(jsonobject.getString("profileUri"));
            user.profilePicUri = dec(jsonobject.getString("profilePicUri"));
            //User Info
            user.firstName = jsonobject.getString("firstName");
            user.lastName = jsonobject.getString("lastName");
            user.phoneNumber = jsonobject.getString("phoneNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    private ArrayList<SpontivlyEventChatMessage> parseEventChatMessages(JSONArray response) {
        ArrayList<SpontivlyEventChatMessage> eventChatMessages = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonobject = response.getJSONObject(i);
                    //Create event data object
                    SpontivlyEventChatMessage e = new SpontivlyEventChatMessage();
                    e.eventId = jsonobject.getInt("eventId");
                    e.posterId = jsonobject.getInt("posterId");
                    e.postedMessage = dec(jsonobject.getString("message"));
                    e.createdAt = jsonobject.getLong("createdAt");
                    if(e.createdAt == -1) {
                        e.createdAt = (long) (System.currentTimeMillis() * .001) - (60 * 15);
                    }
                    // Should get poster's name, first search local DB before doing another server call
                    e.posterFirstName = jsonobject.getString("firstName");
                    e.posterLastName = jsonobject.getString("lastName");

                    eventChatMessages.add(e);

                } catch (JSONException e) { e.printStackTrace(); }
            }
        } catch (Exception e){ Log.e("Spontivly", Log.getStackTraceString(e)); }

        return eventChatMessages;
    }

    String enc(String value) { return AppUtils.urlenc(value);}
    String dec(String value) { return AppUtils.urldec(value);}
    String addParam(String url, String name, boolean value) { return addParam(url, name, value? 1 : 0); }
    String addParam(String url, String name, int value) { return url + "&" + name + "=" + value; }
    String addParam(String url, String name, long value) { return url + "&" + name + "=" + value; }
    String addParam(String url, String name, double value) { return url + "&" + name + "=" + value; }
    String addParam(String url, String name, String value) {
        return url + "&" + name + "=" + enc(value);
    }

}
