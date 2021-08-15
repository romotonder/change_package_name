package com.romo.tonder.visits.models;

public class ListingModel {
    private String listId;
    private String listPostTitle;
    private String listTagLine;
    private String listLogo;
    private String listCoverImg;
    private String listAdd;
    private String listLat;
    private String listLong;
    private String googleMapUrl;
    private String listHourMode;
    private String listPublistDate;
    private String listPeopleInterested;
    private String byUser;
    private String listMode;
    private String listAverage;
    private String listTotleScore;
    private String listDistance;
    private float distanceInNumber;
    private int viewType;
    private String isOpen;

    public float getDistanceInNumber() {
        return distanceInNumber;
    }

    public void setDistanceInNumber(float distanceInNumber) {
        this.distanceInNumber = distanceInNumber;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListPostTitle() {
        return listPostTitle;
    }

    public void setListPostTitle(String listPostTitle) {
        this.listPostTitle = listPostTitle;
    }

    public String getListTagLine() {
        return listTagLine;
    }

    public void setListTagLine(String listTagLine) {
        this.listTagLine = listTagLine;
    }

    public String getListLogo() {
        return listLogo;
    }

    public void setListLogo(String listLogo) {
        this.listLogo = listLogo;
    }

    public String getListCoverImg() {
        return listCoverImg;
    }

    public void setListCoverImg(String listCoverImg) {
        this.listCoverImg = listCoverImg;
    }

    public String getListAdd() {
        return listAdd;
    }

    public void setListAdd(String listAdd) {
        this.listAdd = listAdd;
    }

    public String getListLat() {
        return listLat;
    }

    public void setListLat(String listLat) {
        this.listLat = listLat;
    }

    public String getListLong() {
        return listLong;
    }

    public void setListLong(String listLong) {
        this.listLong = listLong;
    }

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    public void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl;
    }

    public String getListHourMode() {
        return listHourMode;
    }

    public void setListHourMode(String listHourMode) {
        this.listHourMode = listHourMode;
    }

    public String getListPublistDate() {
        return listPublistDate;
    }

    public void setListPublistDate(String listPublistDate) {
        this.listPublistDate = listPublistDate;
    }

    public String getListPeopleInterested() {
        return listPeopleInterested;
    }

    public void setListPeopleInterested(String listPeopleInterested) {
        this.listPeopleInterested = listPeopleInterested;
    }

    public String getByUser() {
        return byUser;
    }

    public void setByUser(String byUser) {
        this.byUser = byUser;
    }

    public String getListMode() {
        return listMode;
    }

    public void setListMode(String listMode) {
        this.listMode = listMode;
    }

    public String getListAverage() {
        return listAverage;
    }

    public void setListAverage(String listAverage) {
        this.listAverage = listAverage;
    }

    public String getListTotleScore() {
        return listTotleScore;
    }

    public void setListTotleScore(String listTotleScore) {
        this.listTotleScore = listTotleScore;
    }

    public String getListDistance() {
        return listDistance;
    }

    public void setListDistance(String listDistance) {
        this.listDistance = listDistance;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

//    @Override
//    public int compareTo(Object o) {
//        int compare = ((ListingModel) o).getDistanceInNumber();
////        return compare - this.distanceInNumber;
//        return this.distanceInNumber - compare;
//    }
}
