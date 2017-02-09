package com.app.gsmademo;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by anil on 1/7/16.
 */
public class GsmaDemoApplication extends Application {

    private static GsmaDemoApplication mInstance;
    private RequestQueue mRequestQueue;
    private String TAG = "GsmaDemo";
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized GsmaDemoApplication getInstance(){
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return  mRequestQueue;
    }

    public void canclePendingRequest(Object tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }

    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

}
