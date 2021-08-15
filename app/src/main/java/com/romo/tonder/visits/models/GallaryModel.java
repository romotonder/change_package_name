package com.romo.tonder.visits.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GallaryModel implements Parcelable {
    private String id;
    private String imgUrl;
    private int viewType;


    public GallaryModel(Parcel in) {
        id = in.readString();
        imgUrl = in.readString();
        viewType = in.readInt();
    }

    public static final Creator<GallaryModel> CREATOR = new Creator<GallaryModel>() {
        @Override
        public GallaryModel createFromParcel(Parcel in) {
            return new GallaryModel(in);
        }

        @Override
        public GallaryModel[] newArray(int size) {
            return new GallaryModel[size];
        }
    };

    public GallaryModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(id);
        dest.writeString(imgUrl);
        dest.writeInt(viewType);
    }
}
