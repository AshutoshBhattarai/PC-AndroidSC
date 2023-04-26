package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PackDetailModel implements Serializable {
    @SerializedName("prod_id")
    int id;
    @SerializedName("prod_name")
    String name;
    @SerializedName("prod_price")
    double price;
    @SerializedName("prod_stock")
    int stock;
    @SerializedName("quantity")
    int quantity;
    @SerializedName("image")
    String image;

    public PackDetailModel() {
    }

    public PackDetailModel(int id, String name, double price, int stock, int quantity, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.quantity = quantity;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
