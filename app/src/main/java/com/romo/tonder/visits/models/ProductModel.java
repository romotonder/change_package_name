package com.romo.tonder.visits.models;

public class ProductModel {
    /* String product_name = jProducts.getString("productName");
                            String productCat = jProducts.getString("productCat");
                            String productCur = jProducts.getString("productCur");
                            String productPrice = jProducts.getString("productPrice");
                            String productUrl = jProducts.getString("productUrl");
                            String productImg */
    private String productName;
    private String productCat;
    private String productCur;
    private String productPrice;
    private String productUrl;
    private String productImg;
    private int viewType;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCat() {
        return productCat;
    }

    public void setProductCat(String productCat) {
        this.productCat = productCat;
    }

    public String getProductCur() {
        return productCur;
    }

    public void setProductCur(String productCur) {
        this.productCur = productCur;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
