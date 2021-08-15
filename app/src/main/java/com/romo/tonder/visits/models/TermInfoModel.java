package com.romo.tonder.visits.models;

public class TermInfoModel {
    private String termID;
    private String termName;
    private String termImage;
    private int viewType;

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermImage() {
        return termImage;
    }

    public void setTermImage(String termImage) {
        this.termImage = termImage;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
