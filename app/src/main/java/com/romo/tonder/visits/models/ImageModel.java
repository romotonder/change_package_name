package com.romo.tonder.visits.models;

public class ImageModel {
    String imgUrl;
    int img;

    public ImageModel() {
    }

    public ImageModel(int img) {
        this.img = img;
    }

    public ImageModel(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
