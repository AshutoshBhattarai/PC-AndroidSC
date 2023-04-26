package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

public class PackModel {
    @SerializedName("pack_id")
    int id;
    @SerializedName("pack_title")
    String title;
    @SerializedName("pack_description")
    String description;
    @SerializedName("pack_img")
    String imgUrl;

    public PackModel() {
    }

    public PackModel(String title, String description, String imgUrl) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "PackModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}


