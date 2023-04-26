package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderModel2 {
    int user_id;
    double total;
    Location location;
    ArrayList<PackPost> orders;
    @SerializedName("delivery_date")
    String Date;

    public OrderModel2(int user_id, double total, Location location, ArrayList<PackPost> orders) {
        this.user_id = user_id;
        this.total = total;
        this.location = location;
        this.orders = orders;
    }

    public OrderModel2() {
    }
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<PackPost> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<PackPost> orders) {
        this.orders = orders;
    }
}
