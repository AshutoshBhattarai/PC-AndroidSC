package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

public class AdminModel {
    @SerializedName("a_name")
    String user;
    @SerializedName("a_password")
    String pass;
    @SerializedName("id")
    int id;
    @SerializedName("a_phone")
    long phone;
    @SerializedName("role")
    String role;
    @SerializedName("token")
    String token;

    public AdminModel() {
    }
    public AdminModel(String user, String pass, int id, long phone) {
        this.user = user;
        this.pass = pass;
        this.id = id;
        this.phone = phone;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AdminModel{" +
                "user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", id=" + id +
                ", phone=" + phone +
                '}';
    }
}
