package com.spontivly.chat.models;

import com.spontivly.chat.utils.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpontivlyUser {


    public SpontivlyUser(){}

    public SpontivlyUser(int userId){
        this.userId = userId;
    }

    public int userId = 1;

    public String googleId = "";
    public String fbUserId = "";
    public String linkedInId = "";
    public String instagramId = "";

    public String profileUri = "";
    public String profilePicUri = "";
    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public String birthdate = "";
    public String gender = "";
    public String phoneNumber = "";


    public boolean hasPhoneNumber() {
        if(phoneNumber == null){return false;}
        String pattern = "\\(?([0-9]{3})\\)?([ .-]?)([0-9]{3})\\2([0-9]{4})";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(phoneNumber);
        boolean found = m.find();
        return found;

    }

    public boolean hasSocialId() {
        return !TextUtils.isEmpty(fbUserId) || !TextUtils.isEmpty(googleId) || !TextUtils.isEmpty(linkedInId) || !TextUtils.isEmpty(instagramId);
    }

    public boolean isGuest() {
        return userId == -1;
    }

    public boolean hasFacebookId() {
        return fbUserId != null && !fbUserId.isEmpty();
    }

    public boolean hasGoogleId() {
        return googleId != null && !googleId.isEmpty();
    }

    public boolean hasLinkedInId() {
        return linkedInId != null && !linkedInId.isEmpty();
    }

    public boolean hasInstagramId() {
        return instagramId != null && !instagramId.isEmpty();
    }

}
