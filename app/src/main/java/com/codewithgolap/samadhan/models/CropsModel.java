package com.codewithgolap.samadhan.models;

public class CropsModel {

    private String cId, name, description, image, mImg, details, temp, dhImage, detailCrop, culture;

    public CropsModel() {
    }

    public CropsModel(String cId, String name, String description, String image,
                      String mImg, String details, String temp, String dhImage, String detailCrop, String culture) {
        this.cId = cId;
        this.name = name;
        this.description = description;
        this.image = image;
        this.mImg = mImg;
        this.details = details;
        this.temp = temp;
        this.dhImage = dhImage;
        this.detailCrop = detailCrop;
        this.culture = culture;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getmImg() {
        return mImg;
    }

    public void setmImg(String mImg) {
        this.mImg = mImg;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getDhImage() {
        return dhImage;
    }

    public void setDhImage(String dhImage) {
        this.dhImage = dhImage;
    }

    public String getDetailCrop() {
        return detailCrop;
    }

    public void setDetailCrop(String detailCrop) {
        this.detailCrop = detailCrop;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }
}
