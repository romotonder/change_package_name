package com.romo.tonder.visits.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Pmodel implements Parcelable {
    String pselected;
    String pname;
    String p_id;

    public Pmodel(String pselected, String pname, String p_id){
        this.pselected=pselected;
        this.pname=pname;
        this.p_id=p_id;
    }

    protected Pmodel(Parcel in) {
        pselected = in.readString();
        pname = in.readString();
        p_id = in.readString();
    }

    public static final Creator<Pmodel> CREATOR = new Creator<Pmodel>() {
        @Override
        public Pmodel createFromParcel(Parcel in) {
            return new Pmodel(in);
        }

        @Override
        public Pmodel[] newArray(int size) {
            return new Pmodel[size];
        }
    };

    public String getPselected() {
        return pselected;
    }

    public void setPselected(String pselected) {
        this.pselected = pselected;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pselected);
        dest.writeString(pname);
        dest.writeString(p_id);
    }
}
