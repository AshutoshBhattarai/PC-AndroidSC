package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

public class ProductModel {
    @SerializedName("prod_id")
    int id;
    @SerializedName("prod_name")
    String name;
    @SerializedName("prod_price")
    double price;
    @SerializedName("prod_stock")
    int stock;
    @SerializedName("image")
    String image;

    public ProductModel() {
    }

    public ProductModel(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "id=" + id +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", name='" + name + '\'' +
                '}';
    }
}
