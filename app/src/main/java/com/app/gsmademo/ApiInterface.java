package com.app.gsmademo;

import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("discovery")
    Call<ResponseBody> hitDiscovery(@FieldMap HashMap<String, String> map);

    /*@FormUrlEncoded
    @POST("hit_token_api.php")
    Call<ResponseBody> hitToken(@FieldMap HashMap<String, String> map);*/

    @FormUrlEncoded
    @POST
    Call<ResponseBody> hitToken(@Url String path, @FieldMap HashMap<String, String> map);


}
