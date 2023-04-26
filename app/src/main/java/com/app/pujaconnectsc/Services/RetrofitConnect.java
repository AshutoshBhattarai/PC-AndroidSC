package com.app.pujaconnectsc.Services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnect {
    private static final String BaseUrl="http://192.168.101.4:5000";
    private static Retrofit retrofit=null;
    private static Retrofit getClient()
    {
        if(retrofit==null)
        {
            HttpLoggingInterceptor log =new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client=new OkHttpClient.Builder().addNetworkInterceptor(log).build();
            retrofit=new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static PackageAPI getPackageApi()
    {
        return getClient().create(PackageAPI.class);
    }
    public static UserAPI getUserApi(){return getClient().create(UserAPI.class);}


}
