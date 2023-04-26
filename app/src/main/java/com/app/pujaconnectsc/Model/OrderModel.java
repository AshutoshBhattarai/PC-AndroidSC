package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

public class OrderModel {
    @SerializedName("id")
    int id;
    @SerializedName("total")
    double total;
    @SerializedName("order_date")
    String date;
    @SerializedName("user_id")
    int userid;
    @SerializedName("status")
    String status;
    @SerializedName("location")
    Location location;
    @SerializedName("otp")
    String otp;
    @SerializedName("delivery_date")
    String deliveryDate;

    public OrderModel() {
    }

    public OrderModel(int id) {
        this.id = id;
    }

    public OrderModel(int id, String code) {
        this.id = id;
        code = otp;
    }

    public OrderModel(int id, double total, String date) {
        this.id = id;
        this.total = total;
        this.date = date;
    }

    public OrderModel(int id, double total, String date, int userid, String status, Location location) {
        this.id = id;
        this.total = total;
        this.date = date;
        this.userid = userid;
        this.status = status;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location getLocation() {
        return location;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "id=" + id +
                ", total=" + total +
                ", date='" + date + '\'' +
                ", userid=" + userid +
                ", status='" + status + '\'' +
                ", location=" + location +
                '}';
    }
}
