package com.example.etsy.myetsy;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static VolleySingleton mVolleyInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mContext;
    private LruBitmapCache mBitmapCache;

    private VolleySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mBitmapCache =  new LruBitmapCache(LruBitmapCache.getCacheSize(context));
        mImageLoader = new ImageLoader(mRequestQueue, mBitmapCache);
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mVolleyInstance == null) {
            mVolleyInstance = new VolleySingleton(context);
        }
        return mVolleyInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // use the application context
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public LruBitmapCache getBitmapCache() { return mBitmapCache; }

}