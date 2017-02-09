package com.app.gsmademo;

import android.util.Base64;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class RestApi {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> aClass, String hitBaseUrl, final boolean isBasicAuth){


        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(hitBaseUrl).addConverterFactory(JacksonConverterFactory.create());
        String credentials = MobileAuthenticationVolley.CLIENT_ID+":"+MobileAuthenticationVolley.CLIENT_SECRET;
        final String basic ="Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException
                {
                    Request original = chain.request();
                    Request.Builder requestBuilder;
                    if(isBasicAuth)
                    {
                        requestBuilder = original.newBuilder()
                                .header("Authorization", basic)
                                .method(original.method(), original.body());
                    }else{
                        requestBuilder = original.newBuilder()
                                .method(original.method(), original.body());
                    }

                    Request request = requestBuilder.build();
                    Response response = chain.proceed(request);
                    return response;
                }
            });
        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();
        return retrofit.create(aClass);
    }

    public static RequestBody getRequestBody(String params){
        return RequestBody.create(MediaType.parse("multipart/form-data"),params);
    }

}
