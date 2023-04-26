package com.app.pujaconnectsc.Model;

import com.google.gson.annotations.SerializedName;

public class CusModel {
    @SerializedName("user_id")
    int id;
    @SerializedName("phone")
    long phone;
    @SerializedName("username")
    String name;
    @SerializedName("password")
    String pass;
    @SerializedName("address")
    String address;
    @SerializedName("role")
    String role;
    @SerializedName("reset_code")
    String reset;
    @SerializedName("email")
    String email;
    @SerializedName("new_pass")
    String newPass;

    public CusModel() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    @Override
    public String toString() {
        return "CusModel{" +
                "id=" + id +
                ", phone=" + phone +
                ", name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                ", reset='" + reset + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
