package com.romo.tonder.visits.models;

public class InterestModels {

    String pselected;
    String pname;
    String p_id;
    int viewType;

    public InterestModels(String pselected, String pname, String p_id,int viewType){
        this.pselected=pselected;
        this.pname=pname;
        this.p_id=p_id;
        this.viewType=viewType;
    }
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

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
