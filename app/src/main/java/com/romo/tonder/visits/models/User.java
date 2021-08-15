package com.romo.tonder.visits.models;

import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {
    public String displayName;
    public String userEmail;
    public String userID;
    public Uri photoURI;
    public String socialPlatform;
    public String birthDay;
    public String lastlatitude;
    public String lastLogitude;

    public String getLastlatitude() {
        return lastlatitude;
    }

    public void setLastlatitude(String lastlatitude) {
        this.lastlatitude = lastlatitude;
    }

    public String getLastLogitude() {
        return lastLogitude;
    }

    public void setLastLogitude(String lastLogitude) {
        this.lastLogitude = lastLogitude;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSocialPlatform() {
        return socialPlatform;
    }

    public void setSocialPlatform(String socialPlatform) {
        this.socialPlatform = socialPlatform;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Uri getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }
}
