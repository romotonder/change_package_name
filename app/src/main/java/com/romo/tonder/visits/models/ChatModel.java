package com.romo.tonder.visits.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatModel implements Parcelable {
    public int userID;
    public String displayName;
    public  String firebaseID;
    public String message;
    public long timestamp;
    public int viewType;

    public String fUser;
    public String sUser;

    public ChatModel(){
    }


    protected ChatModel(Parcel in) {
        userID = in.readInt();
        displayName = in.readString();
        firebaseID = in.readString();
        message = in.readString();
        timestamp = in.readLong();
        viewType = in.readInt();
        fUser = in.readString();
        sUser = in.readString();
    }

    public static final Creator<ChatModel> CREATOR = new Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel in) {
            return new ChatModel(in);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userID);
        dest.writeString(displayName);
        dest.writeString(firebaseID);
        dest.writeString(message);
        dest.writeLong(timestamp);
        dest.writeInt(viewType);
        dest.writeString(fUser);
        dest.writeString(sUser);
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getfUser() {
        return fUser;
    }

    public void setfUser(String fUser) {
        this.fUser = fUser;
    }

    public String getsUser() {
        return sUser;
    }

    public void setsUser(String sUser) {
        this.sUser = sUser;
    }

    public static Creator<ChatModel> getCREATOR() {
        return CREATOR;
    }
}
