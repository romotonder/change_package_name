package com.romo.tonder.visits.models;

public class BusynessHourModel {
    private int viewType;
    private String Id;
    private String objectID;
    private String dayOfWeek;
    private String isOpen;

    private String firstOpenHour;
    private String firstCloseHour;
    private String secondOpenHour;
    private String secondCloseHour;

    private String firstOpenHourUTC;
    private String firstCloseHourUTC;
    private String secondOpenHourUTC;
    private String secondCloseHourUTC;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getFirstOpenHour() {
        return firstOpenHour;
    }

    public void setFirstOpenHour(String firstOpenHour) {
        this.firstOpenHour = firstOpenHour;
    }

    public String getFirstCloseHour() {
        return firstCloseHour;
    }

    public void setFirstCloseHour(String firstCloseHour) {
        this.firstCloseHour = firstCloseHour;
    }

    public String getSecondOpenHour() {
        return secondOpenHour;
    }

    public void setSecondOpenHour(String secondOpenHour) {
        this.secondOpenHour = secondOpenHour;
    }

    public String getSecondCloseHour() {
        return secondCloseHour;
    }

    public void setSecondCloseHour(String secondCloseHour) {
        this.secondCloseHour = secondCloseHour;
    }

    public String getFirstOpenHourUTC() {
        return firstOpenHourUTC;
    }

    public void setFirstOpenHourUTC(String firstOpenHourUTC) {
        this.firstOpenHourUTC = firstOpenHourUTC;
    }

    public String getFirstCloseHourUTC() {
        return firstCloseHourUTC;
    }

    public void setFirstCloseHourUTC(String firstCloseHourUTC) {
        this.firstCloseHourUTC = firstCloseHourUTC;
    }

    public String getSecondOpenHourUTC() {
        return secondOpenHourUTC;
    }

    public void setSecondOpenHourUTC(String secondOpenHourUTC) {
        this.secondOpenHourUTC = secondOpenHourUTC;
    }

    public String getSecondCloseHourUTC() {
        return secondCloseHourUTC;
    }

    public void setSecondCloseHourUTC(String secondCloseHourUTC) {
        this.secondCloseHourUTC = secondCloseHourUTC;
    }
}
