package com.romo.tonder.visits.models;

public class FilterChoiceEventBusModel {
    int position;
    String id;
    String name;
    String filterType;

    public FilterChoiceEventBusModel(int position, String id, String name,String filterType) {
        this.position = position;
        this.id = id;
        this.name = name;
        this.filterType =filterType;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
