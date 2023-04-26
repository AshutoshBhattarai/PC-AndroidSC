package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

public class PackPost {
    @SerializedName("pack_id")
    int packid;
    @SerializedName("prod_id")
    int prodid;
    @SerializedName("quantity")
    int quantity;
    @SerializedName("total")
    double total;

    public PackPost() {
    }

    public PackPost(int packid, int prodid, int quantity, double total) {
        this.packid = packid;
        this.prodid = prodid;
        this.quantity = quantity;
        this.total = total;
    }

    public PackPost(int prodid, int quantity) {
        this.prodid = prodid;
        this.quantity = quantity;
    }

    public int getPackid() {
        return packid;
    }

    public void setPackid(int packid) {
        this.packid = packid;
    }

    public int getProdid() {
        return prodid;
    }

    public void setProdid(int prodid) {
        this.prodid = prodid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    @Override
    public String toString() {
        return "PackPost{" +
                "packid=" + packid +
                ", prodid=" + prodid +
                ", quantity=" + quantity +
                ", total=" + total +
                '}';
    }
}
