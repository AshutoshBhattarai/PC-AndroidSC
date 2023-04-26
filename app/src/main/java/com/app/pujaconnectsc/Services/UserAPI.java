package com.app.pujaconnectsc.Services;

import com.app.pujaconnectsc.Model.AdminModel;
import com.app.pujaconnectsc.Model.CusModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPI {
    @GET("users/all")
    Call<List<CusModel>> getCustomers();
    @GET("users/phone/{phone}")
    Call<List<CusModel>> findUser(@Path("phone") String Phone);
    @POST("users/auth")
    Call<List<CusModel>> authUser(@Body CusModel cusModel);
    @GET("users/id/{id}")
    Call<List<CusModel>> findUserById(@Path("id") String id);
    @POST("users/insert")
    Call<CusModel> saveUser(@Body CusModel cusModel);
    @PUT("users/update/{phone}")
    Call<CusModel> updateUser(@Path("phone") String Phone,@Body CusModel customerReg);
    @POST("admin/auth")
    Call<AdminModel> authAdmin(@Body AdminModel adminModel);
    @GET("admin/auth/{phone}/{pass}")
    Call<List<AdminModel>> findAdmin(@Path("phone") String phone,@Path("pass") String pass);
    @POST("admin/insert")
    Call<AdminModel> saveAdmin(@Body AdminModel regAdmin);
    @POST("admin/auth2")
    Call<AdminModel> autoAuthAdmin(@Body AdminModel adminModel);
    @POST("users/reset/email")
    Call<CusModel> resetPasswordEmail(@Body CusModel cusModel);
    @POST("users/update/name")
    Call<CusModel> updateUserName(@Body CusModel cusModel);
    @POST("users/update/email")
    Call<CusModel> updateUserEmail(@Body CusModel cusModel);
    @POST("users/update/password")
    Call<CusModel> updateUserPassword(@Body CusModel cusModel);
    @POST("users/update/phone")
    Call<CusModel> updateUserPhone(@Body CusModel cusModel);
    @POST("users/update/address")
    Call<CusModel> updateUserAddress(@Body CusModel cusModel);
    @POST("users/change/role")
    Call<CusModel> updateUserRole(@Body CusModel cusModel);
}
