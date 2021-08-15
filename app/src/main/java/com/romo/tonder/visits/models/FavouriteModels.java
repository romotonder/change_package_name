package com.romo.tonder.visits.models;

public class FavouriteModels {
    private String listType;
    private String listID;
    private String listTitle;
    private String listTagline;
    private String coverImage;
    private int viewType;

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public String getListTagline() {
        return listTagline;
    }

    public void setListTagline(String listTagline) {
        this.listTagline = listTagline;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
