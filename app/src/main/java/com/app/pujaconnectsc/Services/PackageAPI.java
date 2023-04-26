package com.app.pujaconnectsc.Services;
import com.app.pujaconnectsc.Model.AdminModel;
import com.app.pujaconnectsc.Model.OrderModel;
import com.app.pujaconnectsc.Model.OrderModel2;
import com.app.pujaconnectsc.Model.PackDetailModel;
import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.Model.PackPost;
import com.app.pujaconnectsc.Model.ProductModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PackageAPI {
    @GET("package/all/")
    Call<List<PackModel>> getPackages();

    @POST("package/insert/")
    Call<PackModel> savePackage(@Body PackModel packReg);

    @PUT("package/delete/{id}")
    Call<PackModel> deletePackage(@Path("id") String id);

    @PUT("package/update/{id}")
    Call<PackModel> updatePackage(@Path("id") String id, @Body PackModel packModel);

    @GET("/product/all")
    Call<List<ProductModel>> getProdcuts();

    @POST("/product/insert")
    Call<ProductModel> insertProducts(@Body ProductModel productModel);

    @POST("/product/update")
    Call<ProductModel> updateProduct(@Body ProductModel productModel);

    @GET("/package/detail/{id}")
    Call<List<PackDetailModel>> getPackDetail(@Path("id") String id);

    @POST("/package/detail/insert/")
    Call<PackPost> insertPackProduct(@Body PackPost packPost);

    @POST("/package/detail/update/")
    Call<PackPost> updatePackProduct(@Body PackPost packPost);

    @POST("/package/detail/delete/")
    Call<PackPost> deletePackProduct(@Body PackPost packPost);

    @POST("/order/insert")
    Call<List<PackPost>> registerArrayyOrder(@Body ArrayList<PackPost> packPost);

    @GET("/order/{id}")
    Call<List<OrderModel>> getUserOrders(@Path("id") String id);

    @GET("/order/detail/{id}")
    Call<List<PackDetailModel>> getOrderDetail(@Path("id") String id);

    @GET("/order/all")
    Call<List<OrderModel>> getAllOrders();

    @POST("/order/order")
    Call<OrderModel2> sendOrder(@Body OrderModel2 orderModel2);

    @POST("/order/delivered")
    Call<OrderModel> changeStatus(@Body OrderModel id);

    @POST("/order/cancel")
    Call<OrderModel2> cancelOrder(@Body OrderModel2 orderModel2);

    @POST("/order/invoice")
    Call<OrderModel> getInvoice(@Body OrderModel orderModel);

    @Multipart
    @POST("/product/insertWimg")
    Call<ResponseBody> insertProdwImg(@Part MultipartBody.Part image,
                                      @Part("name") RequestBody name,
                                      @Part("stock") RequestBody stock,
                                      @Part("price") RequestBody price,
                                      @Part("image") RequestBody imgname);

    @Multipart
    @POST("/product/updateWimg")
    Call<ResponseBody> updateProdwImg(@Part MultipartBody.Part image,
                                      @Part("name") RequestBody name,
                                      @Part("stock") RequestBody stock,
                                      @Part("price") RequestBody price,
                                      @Part("image") RequestBody imgname,
                                      @Part("prod_id")RequestBody prod_id);

    @Multipart
    @POST("/package/updatewImg")
    Call<ResponseBody> updatePackwImg(@Part MultipartBody.Part image,
                                      @Part("name") RequestBody name,
                                      @Part("desc") RequestBody desc,
                                      @Part("image") RequestBody imgname,
                                      @Part("id") RequestBody id);

    @Multipart
    @POST("/package/insertwImg")
    Call<ResponseBody> insertPackwImg(@Part MultipartBody.Part image,
                                      @Part("name") RequestBody name,
                                      @Part("desc") RequestBody desc,
                                      @Part("image") RequestBody imgname);
    @POST("product/delete/{id}")
    Call<ProductModel> deleteProd(@Path("id") String id);
    @POST("/order/confrimdelivery")
    Call<OrderModel> confirmDelivery(@Body OrderModel orderModel);

    @POST("/test/token")
    Call<AdminModel> sendToken(@Body AdminModel adminModel);
}
