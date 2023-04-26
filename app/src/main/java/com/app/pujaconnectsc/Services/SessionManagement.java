package com.app.pujaconnectsc.Services;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences pref, pref2;
    SharedPreferences.Editor editor, admeditor;
    String PREF_NAME = "Session";
    String PREF_NAME_ADMIN = "Admin_Session";
    String id = "User_id",
            name = "User_name",
            pass = "User_password",
            phone = "User_phone",
            address = "User_address",
            status = "Logged_in",
            admin = "Admin_Logged",
            role = "Role",
            email = "Email";

    public SessionManagement(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = context.getSharedPreferences(PREF_NAME_ADMIN, Context.MODE_PRIVATE);
        admeditor = pref2.edit();
    }

    public void saveAdminSession(int id, String name, String pass, Long phone, String role,String email) {
        admeditor.putInt(this.id, id)
                .putString(this.name, name)
                .putString(this.pass, pass)
                .putLong(this.phone, phone)
                .putString(this.role, role)
                .putString(this.email,email)
                .putBoolean(admin, true)
                .commit();
    }

    public void saveLocation(String lat, String lng) {
        editor.putString("lat", lat)
                .putString("lng", lng)
                .putBoolean("location", true)
                .commit();
    }

    public void removeLocation() {
        editor.putString("lat", "").putString("lng", "").putBoolean("location", false).commit();
    }

    public void saveUserSession(int id, String name, String pass, Long phone, String address,String Email) {
        editor.putInt(this.id, id)
                .putString(this.name, name)
                .putString(this.pass, pass)
                .putLong(this.phone, phone)
                .putString(this.address, address)
                .putString(this.role, "Customer")
                .putString(this.email,email)
                .putBoolean(status, true)
                .commit();
    }

    public int getSessionUserId() {
        return pref.getInt(id, 0);
    }

    public String getSessionUserName() {
        return pref.getString(name, "");
    }

    public String getSessionUserPass() {
        return pref.getString(pass, "");
    }

    public int getSessionAdminId() {
        return pref2.getInt(id, 0);
    }

    public Long getSessionUserPhone() {
        return pref.getLong(phone, 0);
    }

    public String getSessionAdminRole() {
        return pref2.getString(role, "");
    }

    public String getSessionUserRole() {
        return pref.getString(role, "");
    }

    public String getSessionUserAddress() {
        return pref.getString(address, "");
    }

    public String getUserLat() {
        return pref.getString("lat", "");
    }
    public String getSessionUserEmail(){return pref.getString(email,"");}
    public String getSessionAdminEmail(){return pref2.getString(email,"");}
    public String getUserLng() {
        return pref.getString("lng", "");
    }

    public boolean isLogged() {
        return pref.getBoolean(status, false);
    }

    public boolean hasLocation() {
        return pref.getBoolean("location", false);
    }

    public boolean isAdminLogged() {
        return pref2.getBoolean(admin, false);
    }

    public void removeUserSession() {
        editor.putInt(id, 0)
                .putString(name, "")
                .putString(pass, "")
                .putLong(phone, 0)
                .putString(address, "")
                .putString(role, "")
                .putBoolean(status, false)
                .commit();
    }

    public void removeAdminSession() {
        admeditor.putInt(this.id, 0)
                .putString(this.name, "")
                .putString(this.pass, "")
                .putLong(this.phone, 0)
                .putString(this.role, "")
                .putBoolean(admin, false)
                .commit();
    }
}

